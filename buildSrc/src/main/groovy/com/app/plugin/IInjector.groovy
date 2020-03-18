package com.app.plugin

import javassist.ClassPool;
import org.gradle.api.Project;

public interface IInjector {
    /**
     * 设置project对象
     */
    void setProject(Project project)

    /**
     * 设置variant目录关键串（感觉暂时不会用到）
     * @param variantDir
     */
    void setVariantDir(String variantDir)

    /**
     * 注入器名称
     * @return
     */
    def name()

    /**
     * 对dir目录中的class进行注入
     * @param pool
     * @param dir
     * @param config
     * @return
     */
//   def injectClass(ClassPool pool,String dir,Map config)
    def injectClass(String className, String path)


}
