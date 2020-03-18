package com.app.plugin.manifest

public class ManifestReader implements IManifest {

    //AndroidManifest文件路径
    def final filePath
    def manifest

    public ManifestReader(String path) {
        filePath = path
    }

    @Override
    List<String> getActivities() {
        init()

        def activity = []
        String pkg = manifest.@package
        manifest.application.activity.each {
            String name = it.'@android:name'
            if (name.substring(0, 1) == '.') {
                name = pkg + name
            }
            activities << name
        }
        activities
    }

    @Override
    String getPackageName() {
        init()
        manifest.@package
    }

    def private init() {
        if (manifest == null) {
            manifest = new XmlSlurper().parse(filePath)
        }
    }
}