package com.ahasbini.tools.androidopencv.internal.service

import com.ahasbini.tools.androidopencv.internal.util.Logger
import groovy.transform.PackageScope
import org.gradle.api.Project
/**
 * Created by ahasbini on 12-Oct-19.
 */
class AndroidBuildScriptModifier {
    // TODO: 14-Oct-19 ahasbini: implement testing for this

    private final Logger logger = Logger.getLogger(AndroidBuildScriptModifier)

    private Project project

    @PackageScope
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
        // TODO: 19-Oct-19 ahasbini: Implement test to validate the below

        if (path == null || configurationName == null || library == null) {
            throw new NullPointerException("Parameters can't be null: path=${path}, " +
                    "configurationName=${configurationName}, library=${library}")
        }

        logger.info("Adding dependency {} {} in {}", configurationName, library,
                path.getAbsolutePath())

        String repoName = "AndroidOpenCVFlatDir " + path.getPath()
        boolean isRepoExisting = false
        project.repositories.each {
            if (it.name == repoName) {
                logger.info("Found repo '{}' already existing", it.name)
                isRepoExisting = true
                return
            }
        }

        if (!isRepoExisting) {
            project.repositories {
                flatDir {
                    name repoName
                    dirs path
                }
            }
        }

        project.repositories.each {
            logger.info("Final repo in project: {}", it.name)
        }

        boolean isDependencyExisting = false
        project.configurations.getByName(configurationName).getDependencies().each {
            String existingLibrary = it.getGroup() + ":" + it.getName() + ":" + it.getVersion() +
                    "@aar"

            if (existingLibrary == library) {
                logger.info("Found library {} in configuration {} already existing",
                        existingLibrary, configurationName)
                isDependencyExisting = true
                return
            }
        }

        if (!isDependencyExisting) {
            project.dependencies.add(configurationName, library)
        }

        project.configurations.each {
            def existingConfigurationName = it.getName()
            it.getDependencies().each {
                logger.info("Final dependency in configuration: {} {}",
                        existingConfigurationName, it.toString())
            }
        }
    }
}
