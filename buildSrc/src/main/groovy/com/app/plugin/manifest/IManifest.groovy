package com.app.plugin.manifest

import org.gradle.model.internal.inspect.StructNodeInitializer

public interface IManifest {

    /**
     * 获取AndroidManifest中声明的所有Activity
     * @return
     */
    List<String> getActivities()

    /**
     * 应用程序包名
     * @return
     */
    String getPackageName()
}