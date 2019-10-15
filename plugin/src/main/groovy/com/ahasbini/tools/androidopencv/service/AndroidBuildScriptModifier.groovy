package com.ahasbini.tools.androidopencv.service

import com.ahasbini.tools.androidopencv.logging.Logger
import org.gradle.api.Project
/**
 * Created by ahasbini on 12-Oct-19.
 */
class AndroidBuildScriptModifier {
    // TODO: 14-Oct-19 ahasbini: implement testing for this

    private final Logger logger = Logger.getLogger(AndroidBuildScriptModifier)

    private Project project

    AndroidBuildScriptModifier(Project project) {
        this.project = project
    }

    void modifyAndroidBuildScript() throws Exception {
        logger.info("Modifying android buildscript")
        def androidExtension = project.getExtensions().findByName("android")
        String opencvDir = new File(project.getBuildDir(), "androidopencv").getPath()

        // TODO: 14-Oct-19 ahasbini: implement tests to validate below
        androidExtension.getDefaultConfig().getExternalNativeBuildOptions().cmake {
            if (!cppFlags.contains('-frtti')) {
                cppFlags.add('-frtti')
            }

            if (!cppFlags.contains('-fexceptions')) {
                cppFlags.add('-fexceptions')
            }

            arguments.add("-DOpenCV_DIR=${opencvDir}/sdk/native/jni".toString())
        }

        // TODO: 14-Oct-19 ahasbini: implement tests to validate below
        androidExtension.sourceSets.main {
            java.srcDirs = [java.srcDirs, "${opencvDir}/sdk/java/src"].flatten()
            java.exclude "${opencvDir}/sdk/java/src/org/opencv/engine/OpenCVEngineInterface.aidl"
            res.srcDirs = [res.srcDirs, "${opencvDir}/sdk/java/res"].flatten()
            assets.srcDirs = [assets.srcDirs, "${opencvDir}/sdk/etc"].flatten()
            aidl.srcDirs = [
                    aidl.srcDirs,
                    "${opencvDir}/sdk/java/src/org/opencv/engine/OpenCVEngineInterface.aidl"
            ].flatten()
            jni.srcDirs = [jni.srcDirs, "${opencvDir}/sdk/native/jni/include"].flatten()
            jniLibs.srcDirs = [jniLibs.srcDirs, "${opencvDir}/sdk/native/3rdparty/libs",
                               "${opencvDir}/sdk/native/libs",
                               "${opencvDir}/sdk/native/staticlibs"].flatten()
        }
    }
}
