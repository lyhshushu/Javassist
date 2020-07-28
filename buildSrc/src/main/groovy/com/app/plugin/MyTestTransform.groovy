package com.app.plugin

import com.android.build.api.transform.*
import com.android.utils.FileUtils
import com.google.common.collect.Sets
import javassist.ClassPool
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

public class MyTestTransform extends Transform {

    Project project
    // 添加构造，为了方便从plugin中拿到project对象
    public MyTestTransform(Project project) {
        this.project = project
    }


    @Override
    String getName() {
        return "preDex";
//        return Transform.simpleName;
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
//        return TransformManager.CONTENT_CLASS
        return Sets.immutableEnumSet(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
//        return TransformManager.SCOPE_FULL_PROJECT
        //含有一个失效方法待修正
        return Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT, QualifiedContent.Scope.PROJECT_LOCAL_DEPS,
                QualifiedContent.Scope.SUB_PROJECTS, QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS, QualifiedContent.Scope.EXTERNAL_LIBRARIES)
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {

        def startTime = System.currentTimeMillis()

        //添加module
        //获取hack module的debug目录，也就是Antilazy.class所在的目录(之前验证的代码)
        def libPath = project.project(':hack').buildDir.absolutePath.concat("\\intermediates\\javac\\debug")
        //将路径添加到ClassPool的classPath中
        Inject.appendClasspath(libPath)

        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                // 获取output目录
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                //TODO 对input文件做处理
                Inject.injectDir(directoryInput.file.absolutePath)

                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            input.jarInputs.each { JarInput jarInput ->

                //TODO 对input文件做处理（如代码注入）
                String jarPath = jarInput.file.absolutePath
                String projectName = project.rootProject.name
                if (jarPath.endsWith("classes.jar") && jarPath.contains("exploded-aar" + "\\" + projectName)) {
                    Inject.injectJar(jarPath)
                }

                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
        // inputs就是输入文件的集合
        // outputProvider可以获取outputs的路径

        ClassPool.getDefault().clearImportedPackages()
        //答应时间提示transform完成
        project.logger.error("javassistTransform Cast:" + (System.currentTimeMillis() - startTime) / 1000 + "second")
    }
}