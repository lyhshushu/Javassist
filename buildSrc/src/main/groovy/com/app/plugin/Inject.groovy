package com.app.plugin

import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import org.apache.commons.io.FileUtils

/**
 * 注入代码的2中情况，一种是目录，需要遍历里面的class进行注入
 * 另外一种是jar包，需要先解压jar包，注入代码后重新打包成jar
 */
public class Inject {

//    def private static LOADER_PROP_FILE = 'loader_activities.properties'

    def private static changeActivityRules = [
            "com.example.javassist.BaseActivity"   : "com.example.javassist.BaseNewActivity",
            "com.example.javassist.InsteadActivity": "android.app.Activity",
            "com.example.javassist.CatSay"         : "com.example.javassist.DogSay",
    ]

//    @Override
//    def injectClass(ClassPool pool, String dir, Map config) {
//        init()
//        /* 遍历程序中声明的所有 Activity */
//        //每次都new一下，否则多个variant一起构建时只会获取到首个manifest
//        new ManifestAPI().getActivities(project,variantDir).each {
//            //处理没有被忽略的Activity
//            if(!(it in CommonData.ignoredActivities)){
//
//            }
//        }
//    }

    private static ClassPool pool = ClassPool.getDefault();
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
        pool.insertClassPath("C:\\Users\\4399\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar")
//      尝试去找到androidX的包（appCompatActivity）
//        pool.insertClassPath("C:\\Users\\4399\\AppData\\Local\\Android\\Sdk\\build-tools\\29.0.2\\renderscript\\lib\\androidx-rs.jar")
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
                        injectClass(className, path)
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
        CtClass c = pool.get(className)
        //当前父类
        def originSuperCls = c.superclass;
//        当前Activity向上回溯，直到找到要替换的Activity
        def superCls = originSuperCls;
        while (superCls != null && !(superCls.name in changeActivityRules.keySet())) {
            c = superCls
            superCls = c.superclass
        }
        //问题：如果此类中的父类又在key中就无法替换到
        if (c.name in changeActivityRules.values()) {
            return;
        }

        if (superCls != null) {
            //开始插入代码（activity如果修改继承直接插入会出错）
            CtClass cParent = pool.get(changeActivityRules.get(superCls.name))
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

            //单独方法针对
            c.setSuperclass(cParent)
            c.getMethods().each { outerMethod ->
                outerMethod.instrument(new ExprEditor() {
                    @Override
                    void edit(MethodCall call) throws CannotCompileException {
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


//    def private init() {
//        //延迟初始化
//        //todo 从配置读取，不写死在代码中
//        if (changeActivityRules == null) {
//            def buildSrcPath = project.project(':buildSrc').projectDir.absolutePath
//            def loaderConfigPath = String.join(File.separator, buildSrcPath, 'res', LOADER_PROP_FILE)
//
//            changeActivityRules = new Properties()
//            new File(loaderConfigPath).withInputStream {
//                changeActivityRules.load(it)
//            }
//            println '\n>>> Activity Rules：'
//            loaderActivityRules.each {
//                println it
//            }
//            println()
//        }
//    }
}