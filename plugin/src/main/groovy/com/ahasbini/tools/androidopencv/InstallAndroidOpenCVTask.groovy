package com.ahasbini.tools.androidopencv

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * Created by ahasbini on 11-Oct-19.
 */
class InstallAndroidOpenCVTask extends DefaultTask {

    public final static String NAME = "installAndroidOpenCV"

    private final Logger logger = LoggerFactory.getLogger(InstallAndroidOpenCVTask)
    private final ResourceBundle messages = ResourceBundle.getBundle("messages");

    @Override
    String getName() {
        return NAME
    }

    @Override
    String getDescription() {
        return "Downloads, links and configures Android project to include Android OpenCV libs"
    }

    @TaskAction
    performInstall() {
        def androidExtension = getProject().getExtensions().findByName("android")
        def androidOpenCVExtension = (AndroidOpenCVExtension) getProject().getExtensions()
                .findByName("androidOpenCV")

        logger.info("AndroidOpenCVExtension: " + androidOpenCVExtension.toString())
        if (androidOpenCVExtension == null || androidOpenCVExtension.getVersion() == null
                || androidOpenCVExtension.getVersion() == "") {
            throw new PluginException(messages.getString("missing_opencv_version"))
        }


        logger.info("android: " + androidExtension)
        //noinspection GroovyAssignabilityCheck
        logger.info("android.compileSdkVersion: " + androidExtension.compileSdkVersion)
    }
}
