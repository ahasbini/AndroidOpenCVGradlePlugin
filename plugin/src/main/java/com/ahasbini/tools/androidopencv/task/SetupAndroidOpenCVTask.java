package com.ahasbini.tools.androidopencv.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.NonNullApi;

/**
 * This is an empty task used to group all the other tasks
 * <p>
 * Created by ahasbini on 27-Nov-19.
 */
@NonNullApi
public class SetupAndroidOpenCVTask extends DefaultTask {

    @Override
    public String getName() {
        return "setupAndroidOpenCV";
    }

    @Override
    public String getDescription() {
        return "Configures and installs AndroidOpenCV dependencies for project.";
    }

    @Override
    public String getGroup() {
        return "AndroidOpenCV";
    }
}
