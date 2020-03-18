package com.app.plugin

public class CommonData {
    /** 保存类文件名和 class 文件路径的关系 */
    def static classAndPath = [:]

    /** App Module 的名称, 如 ':app', 传 '' 时，使用项目根目录为 App Module */
    def static String appModule

    def static String appPackage

    /** 执行 LoaderActivity 替换时，不需要替换的 Activity */
    def static ignoredActivities = []

    def static putClassAndPath(def className, def classFilePath) {
        classAndPath.put(className, classFilePath)
    }

    def static getClassPath(def className) {
        return classAndPath.get(className)
    }
}