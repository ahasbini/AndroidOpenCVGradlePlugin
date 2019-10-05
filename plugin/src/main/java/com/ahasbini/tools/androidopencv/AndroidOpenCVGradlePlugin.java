package com.ahasbini.tools.androidopencv;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ahasbini on 12-Sep-19.
 */
public class AndroidOpenCVGradlePlugin implements Plugin<Project> {

    private final Logger logger = LoggerFactory.getLogger(AndroidOpenCVGradlePlugin.class);

    @Override
    public void apply(Project project) {
//        project.getRepositories().add(0, new AndroidOpenCVRepositoryHandler());
        for (ArtifactRepository repository : project.getRepositories()) {
            logger.debug("repo: {}", repository);
        }
        for (Dependency implementation : project.getConfigurations().getByName("implementation")
                .getAllDependencies()) {
            logger.debug("name: {}, group: {}, version: {}", implementation.getName(),
                    implementation.getGroup(), implementation.getVersion());

        }
    }
}
