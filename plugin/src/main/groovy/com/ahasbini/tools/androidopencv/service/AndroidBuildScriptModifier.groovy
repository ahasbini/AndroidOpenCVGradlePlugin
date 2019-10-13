package com.ahasbini.tools.androidopencv.service

import com.ahasbini.tools.androidopencv.logging.Logger
import org.gradle.api.Project
/**
 * Created by ahasbini on 12-Oct-19.
 */
class AndroidBuildScriptModifier {

    private final Logger logger = Logger.getLogger(AndroidBuildScriptModifier)

    private Project project

    AndroidBuildScriptModifier(Project project) {
        this.project = project
    }

    void modifyAndroidBuildScript() throws Exception {
        logger.info("Modifying android buildscript")
        def androidExtension = project.getExtensions().findByName("android")
        String opencvDir = new File(project.getBuildDir(), "androidopencv").getPath()

        androidExtension.getDefaultConfig().getExternalNativeBuildOptions().cmake {
            if (!cppFlags.contains('-frtti')) {
                cppFlags.add('-frtti')
            }

            if (!cppFlags.contains('-fexceptions')) {
                cppFlags.add('-fexceptions')
            }

            arguments.add("-DOpenCV_DIR=${opencvDir}/sdk/native/jni".toString())
        }

        androidExtension.sourceSets {
            main {
                java.srcDirs = [java.srcDirs, "${opencvDir}/sdk/java/src"]
                java.exclude "${opencvDir}/sdk/java/src/org/opencv/engine/OpenCVEngineInterface.aidl"
                res.srcDirs = [java.srcDirs, "${opencvDir}/sdk/java/res"]
                assets.srcDirs = [assets.srcDirs, "${opencvDir}/sdk/etc"]
                aidl.srcDirs = [aidl.srcDirs, "${opencvDir}/sdk/java/src/org/opencv/engine/OpenCVEngineInterface.aidl"]
                jni.srcDirs = [jni.srcDirs, "${opencvDir}/sdk/native/jni/include"]
                jniLibs.srcDirs = [jniLibs.srcDirs, "${opencvDir}/sdk/native/3rdparty/libs",
                                   "${opencvDir}/sdk/native/libs",
                                   "${opencvDir}/sdk/native/staticlibs"]
            }
        }
    }
}
