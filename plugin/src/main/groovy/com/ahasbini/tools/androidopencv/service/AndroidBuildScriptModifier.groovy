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
    private ArrayList<File> libraryPaths
    private Map

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
    }

    void addLibrary(File path, String configurationName, String library) {
        if (path == null || configurationName == null || library == null) {
            throw new NullPointerException("Parameters can't be null: path=${path}, " +
                    "configurationName=${configurationName}, library=${library}")
        }

        logger.info("Adding dependency {} {} in {}", configurationName, library,
                path.getAbsolutePath())

        // TODO: 19-Oct-19 ahasbini: check if repo exists before adding
        project.repositories {
            flatDir {
                dirs path
            }
        }

        project.repositories.each {
            println it.dump()
            println it.name
        }

        project.dependencies.add(configurationName, library)

        project.dependencies.each {
            println it.dump()
            println it.toString()
        }
    }
}
