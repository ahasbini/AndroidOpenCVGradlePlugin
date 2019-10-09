package com.ahasbini.tools.androidopencv;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ahasbini on 12-Sep-19.
 */
public class AndroidOpenCVGradlePlugin implements Plugin<Project> {

    private final Logger logger = LoggerFactory.getLogger(AndroidOpenCVGradlePlugin.class);

    @Override
    public void apply(Project project) {
        // TODO: 06-Oct-19 ahasbini: implement check for android gradle plugin (application or library)
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

        PluginContainer plugins = project.getPlugins();
        if (plugins.hasPlugin("com.android.application") ||
                plugins.hasPlugin("com.android.library") ||
                plugins.hasPlugin("com.android.test") ||
                plugins.hasPlugin("com.android.feature")) {
            logger.info("Found android gradle plugin");
        } else {
            throw new RuntimeException("No android gradle plugins found, maybe missing\n" +
                    "   apply plugin: 'com.android.application'\n" +
                    "in 'build.gradle' file?");
        }
    }
}
