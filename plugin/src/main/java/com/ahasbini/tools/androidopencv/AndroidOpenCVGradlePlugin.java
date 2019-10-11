package com.ahasbini.tools.androidopencv;

import com.ahasbini.tools.androidopencv.logging.Logger;

import org.gradle.api.Action;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;

import java.util.ResourceBundle;

/**
 * Created by ahasbini on 12-Sep-19.
 */
@NonNullApi
public class AndroidOpenCVGradlePlugin implements Plugin<Project> {

    private final Logger logger = Logger.getLogger(AndroidOpenCVGradlePlugin.class);
    private final ResourceBundle messages = ResourceBundle.getBundle("messages");

    @Override
    public void apply(Project project) {
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

        // Add the extension to the project and wait for afterEvaluate is called when configuration
        // phase is finished
        project.getExtensions().create("androidOpenCV", AndroidOpenCVExtension.class);
        project.afterEvaluate(new AfterEvaluateAction());
    }

    private class AfterEvaluateAction implements Action<Project> {

        @Override
        public void execute(Project project) {
            logger.info("afterEvaluate called");
            AndroidOpenCVExtension androidOpenCVExtension =
                    project.getExtensions().findByType(AndroidOpenCVExtension.class);

            logger.info("androidOpenCVExtension: " + androidOpenCVExtension);

            if (androidOpenCVExtension == null || androidOpenCVExtension.getVersion() == null
                    || androidOpenCVExtension.getVersion().equals("")) {
                throw new PluginException(messages.getString("missing_opencv_version"));
            }

            logger.info("Required OpenCV version: " + androidOpenCVExtension.getVersion());
        }
    }

}
