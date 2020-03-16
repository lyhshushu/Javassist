package com.app.plugin

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension


public class MyTestPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {

        def isApp = project.plugins.hasPlugin(AppPlugin)
        if(isApp){
            def android = project.extensions.findByType(AppExtension)
            android.registerTransform(new MyTestTransform(project))
            project.logger.error "================\n自定义插件成功！开始修改Class文件\n================"
        }
    }
}