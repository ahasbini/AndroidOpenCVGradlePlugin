package com.ahasbini.tools.androidopencv;

import com.ahasbini.tools.androidopencv.logging.Logger;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.PluginManager;

import java.util.ResourceBundle;

/**
 * Created by ahasbini on 12-Sep-19.
 */
public class AndroidOpenCVGradlePlugin implements Plugin<Project> {

    private final Logger logger = Logger.getLogger(AndroidOpenCVGradlePlugin.class);
    private final ResourceBundle messages = ResourceBundle.getBundle("messages");

    @Override
    public void apply(Project project) {
        // TODO: 06-Oct-19 ahasbini: extract required version of opencv
        // TODO: 06-Oct-19 ahasbini: download opencv
        // TODO: 06-Oct-19 ahasbini: add opencv to correct source sets of android project
        // TODO: 06-Oct-19 ahasbini: version format: major.minor.subminor.subsubminor
        // TODO: 06-Oct-19 ahasbini: note the differences between the directories of the old versions and new versions
        // TODO: 06-Oct-19 ahasbini: optional url config for the directory of opencv (prebuilt or others...)

        String enableAndroidOpencvLogs = project.getGradle().getStartParameter()
                .getProjectProperties().get("ENABLE_ANDROID_OPENCV_LOGS");
        if (enableAndroidOpencvLogs != null) {
            logger.quiet("AndroidOpenCVPlugin logs enabled");
            Logger.setUseQuietLogs(true);
        }

        // Check if project has Android Gradle Plugin
        PluginManager plugins = project.getPluginManager();
        if (!plugins.hasPlugin("com.android.application") &&
                !plugins.hasPlugin("com.android.library") &&
                !plugins.hasPlugin("com.android.test") &&
                !plugins.hasPlugin("com.android.feature")) {
            throw new PluginException(messages.getString("missing_android_gradle_plugin"));
        }

        logger.debug("Found android gradle plugin");

        // Add the extension to the project
        project.getExtensions().create("androidOpenCV", AndroidOpenCVExtension.class);

        // Add the task to be later executed by Gradle but placed before Android Gradle Plugin tasks
        // are executed
        InstallAndroidOpenCVTask installAndroidOpenCVTask =
                project.getTasks().create(InstallAndroidOpenCVTask.NAME, InstallAndroidOpenCVTask.class);
        Task preBuild = project.getTasks().findByPath("preBuild");
        if (preBuild != null && preBuild.getEnabled()) {
            preBuild.dependsOn(installAndroidOpenCVTask);
        } else {
            // If unable to place tasks before Android Gradle Plugin tasks, give the user a notice
            // TODO: 11-Oct-19 ahasbini: test this
            logger.quiet("Failed to properly configure android opencv for current build.\n");
        }
    }
}
