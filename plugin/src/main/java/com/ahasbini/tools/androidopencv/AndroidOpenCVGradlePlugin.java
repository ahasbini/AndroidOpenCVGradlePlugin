package com.ahasbini.tools.androidopencv;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * Created by ahasbini on 12-Sep-19.
 */
public class AndroidOpenCVGradlePlugin implements Plugin<Project> {

    private final Logger logger = LoggerFactory.getLogger(AndroidOpenCVGradlePlugin.class);
    private final ResourceBundle messages = ResourceBundle.getBundle("messages");

    @Override
    public void apply(Project project) {
        // TODO: 06-Oct-19 ahasbini: extract required version of opencv
        // TODO: 06-Oct-19 ahasbini: download opencv
        // TODO: 06-Oct-19 ahasbini: add opencv to correct source sets of android project
        // TODO: 06-Oct-19 ahasbini: version format: major.minor.subminor.subsubminor
        // TODO: 06-Oct-19 ahasbini: note the differences between the directories of the old versions and new versions
        // TODO: 06-Oct-19 ahasbini: optional url config for the directory of opencv (prebuilt or others...)

//        project.getRepositories().add(0, new AndroidOpenCVRepositoryHandler());
//        for (ArtifactRepository repository : project.getRepositories()) {
//            logger.debug("repo: {}", repository);
//        }
//        for (Dependency implementation : project.getConfigurations().getByName("implementation")
//                .getAllDependencies()) {
//            logger.debug("name: {}, group: {}, version: {}", implementation.getName(),
//                    implementation.getGroup(), implementation.getVersion());
//
//        }

        PluginManager plugins = project.getPluginManager();
        if (!plugins.hasPlugin("com.android.application") &&
                !plugins.hasPlugin("com.android.library") &&
                !plugins.hasPlugin("com.android.test") &&
                !plugins.hasPlugin("com.android.feature")) {
            throw new PluginException(messages.getString("missing_android_gradle_plugin"));
        }

        logger.debug("Found android gradle plugin");
        project.getExtensions().create("androidOpenCV", AndroidOpenCVExtension.class);

        InstallAndroidOpenCVTask installAndroidOpenCVTask =
                project.getTasks().create(InstallAndroidOpenCVTask.NAME, InstallAndroidOpenCVTask.class);
        Task preBuild = project.getTasks().findByPath("preBuild");
        if (preBuild != null && preBuild.getEnabled()) {
            preBuild.dependsOn(installAndroidOpenCVTask);
        } else {
            // TODO: 11-Oct-19 ahasbini: test this
            logger.warn("Failed to properly configure android opencv for current build.\n");
        }

//        project.getTasks().add()

//        Object androidOpenCV = project.getExtensions().findByName("androidOpenCV");
//        if (androidOpenCV == null) {
//            throw new PluginException(messages.getString("missing_opencv_version"));
//        }
//
//        AndroidOpenCVExtension androidOpenCVExtension = ((AndroidOpenCVExtension) androidOpenCV);
//        logger.debug("android extension: " + androidOpenCVExtension);
//        logger.debug("android.openCVVersion extension: " + androidOpenCVExtension.getVersion());
//        if (androidOpenCVExtension.getVersion() == null ||
//                androidOpenCVExtension.getVersion().equals("")) {
//            throw new PluginException(messages.getString("missing_opencv_version"));
//        }
    }
}
