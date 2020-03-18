package com.app.plugin

import org.gradle.api.Project
import org.gradle.api.internal.project.IProjectFactory

public abstract class BaseInjector implements IInjector{
    protected Project project
    protected String variantDir

    @Override
    public Object name(){
        return getClass().getSimpleName()
    }

    void setProject(Project project) {
        this.project = project
    }

    void setVariantDir(String variantDir) {
        this.variantDir = variantDir
    }
}