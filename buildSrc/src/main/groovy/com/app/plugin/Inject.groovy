package com.app.plugin

import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.expr.ConstructorCall
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import org.apache.commons.io.FileUtils
import org.gradle.internal.impldep.org.yaml.snakeyaml.constructor.Construct


/**
 * 注入代码的2中情况，一种是目录，需要遍历里面的class进行注入
 * 另外一种是jar包，需要先解压jar包，注入代码后重新打包成jar
 */
public class Inject {

    private static ClassPool pool = ClassPool.getDefault()
    /**
     * 添加classpool到Classpool
     * @param libpath
     */
    public static void appendClasspath(String libpath) {
        pool.appendClassPath(libpath)
    }

    /**
     * 遍历目录下所有class，对class进行代码注入
     * 其中以下class不需要注入
     * ——1.R文件相关
     * ——2.配置文件相关（BuildConfig）
     * ——3.Application
     * @param path 目录路径
     */
    public static void injectDir(String path) {
        pool.appendClassPath(path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath;
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")
                        //这里application的名字可以通过解析清单文件获得，此处先写死
                        && !filePath.contains("PatchApplication.class")) {
                    //这里是应用包名，也可从清单文件中获取
                    int index = filePath.indexOf("com\\example\\javassist")
                    if (index != -1) {
                        int end = filePath.length() - 6;
                        String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
                        if (className == "com.example.javassist.MainActivity") {
                            injectClass(className, path)
                        }
                    }
                }
            }
        }
    }

    public static void injectJar(String path) {
        if (path.endsWith(".jar")) {
            File jarFile = new File(path)

            //jar解压后的保存路径
            String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace('.jar', '')
            //解压jar包返回jar包中所有的class的完整类名的集合（带.class后缀）
            List classNameList = JarZipUtil.unzipJar(path, jarZipDir)

            //删除原来的jar包
            jarFile.delete()

            //注入代码
            pool.appendClassPath(jarZipDir)
            for (String className : classNameList) {

                if (className.endsWith(".class")
                        && !className.contains('R$')
                        && !className.contains('R.class')
                        && !className.contains("BuildConfig.class")) {
                    className = className.substring(0, className.length() - 6)
                    injectClass(className, jarZipDir)
                }
            }
            //重新打包jar
            JarZipUtil.zipJar(jarZipDir, path)
            //删除目录
            FileUtils.deleteDirectory(new File(jarZipDir))
        }

    }

    /**
     * 指定要插入class的位置
     * @param className
     * @param path
     */
    private static void injectClass(String className, String path) {
//        CtClass c = pool.getCtClass(className)
        CtClass c = pool.get("com.example.javassist.MainActivity")
        //当前父类
        def originSuperCls = c.superclass;
        //当前Activity向上回溯，直到找到要替换的Activity
        def superCls = originSuperCls;
        while (superCls != null && !(superCls.name == "com.example.javassist.BaseActivity")) {
            c = superCls
            superCls = c.superclass
        }
        if (c.name == "com.example.javassist.BaseNewActivity") {
            return;
        }

        if (superCls != null) {
            //开始插入代码（activity如果修改继承直接插入会出错）
            CtClass cParent = pool.get("com.example.javassist.BaseNewActivity")
            //解冻
            if (c.isFrozen()) {
                c.defrost()
            }
            //插入代码语句
//        ctMethod.insertAt(23,"return super.say();")
//        修改构建类代码
//        def constructor = c.getConstructors()[0]
//        constructor.insertAfter("System.out.println(com.example.javassist.AntilazyLoad.class);")
//        constructor.insertBefore("System.out.println(\"sadasdada\");")

            c.setSuperclass(cParent)

            c.getDeclaredMethods().each { outerMethod ->
                outerMethod.instrument(new ExprEditor() {
                    @Override
                    void edit(ConstructorCall call) throws CannotCompileException {
                        if (call.isSuper()) {
                            if (call.getMethod().getReturnType().getName() == 'void') {
                                call.replace('{super.' + call.getMethodName() + '($$);}')
                            } else {
                                call.replace('{$_=super.' + call.getMethodName() + '($$);}')
                            }
                        }
                    }
                })
            }
//       c.setSuperclass(pool.get("com.example.javassist.DogSay"))
            c.writeFile(path)
            c.detach()
        }
    }
}