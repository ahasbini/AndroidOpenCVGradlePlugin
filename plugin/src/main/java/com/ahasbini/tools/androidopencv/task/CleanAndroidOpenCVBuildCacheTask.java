package com.ahasbini.tools.androidopencv.task;

import com.ahasbini.tools.androidopencv.AndroidOpenCVExtension;
import com.ahasbini.tools.androidopencv.PluginException;
import com.ahasbini.tools.androidopencv.internal.service.FilesManager;
import com.ahasbini.tools.androidopencv.internal.service.Injector;
import com.ahasbini.tools.androidopencv.internal.util.ExceptionUtils;
import com.ahasbini.tools.androidopencv.internal.util.Logger;

import org.gradle.api.DefaultTask;
import org.gradle.api.NonNullApi;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by ahasbini on 27-Nov-19.
 */
@NonNullApi
public class CleanAndroidOpenCVBuildCacheTask extends DefaultTask {

    private final Logger logger = Logger.getLogger(CleanAndroidOpenCVBuildCacheTask.class);
    private final ResourceBundle messages = Injector.getMessages();

    private boolean all = false;
    private String version = null;

    @SuppressWarnings({"UnstableApiUsage", "unused"})
    @Option(option = "all", description = "Cleans all versions")
    public void setAll() {
        logger.debug("setAll called");

        this.all = true;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Option(option = "version",
            description = "Cleans the specified version instead of the version defined in " +
                    "build.gradle androidOpenCV block")
    public void setVersion(String version) {
        logger.debug("setVersion called, version=" + version);

        this.version = version;
    }

    @TaskAction
    public void cleanAndroidOpenCVBuildCache() {
        logger.debug("cleanAndroidOpenCVBuildCache called");

        Project project = getProject();
        FilesManager filesManager = Injector.getFilesManager(project);

        File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                ".androidopencv");
        if (!filesManager.checkOrCreateDirectory(androidOpenCVCacheDir)) {
            throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                    androidOpenCVCacheDir.getAbsolutePath()));
        }

        if (all) {
            logger.info("cleaning build-cache for all versions of Android OpenCV");

            File[] files = androidOpenCVCacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.exists()) {
                        try {
                            filesManager.recursiveDelete(file);
                        } catch (IOException e) {
                            throw new PluginException("Unable to delete AndroidOpenCV " +
                                    "build-cache files.\n" +
                                    ExceptionUtils.getCauses(e,
                                            messages.getString("caused_by")), e);
                        }
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                }
            }
        } else {
            String requestedVersion;

            if (version == null) {
                AndroidOpenCVExtension androidOpenCVExtension = project.getExtensions()
                        .getByType(AndroidOpenCVExtension.class);

                requestedVersion = androidOpenCVExtension.getVersion();
            } else {
                requestedVersion = version;
            }

            logger.info("cleaning build-cache for version " + requestedVersion + " of Android OpenCV");

            File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);
            if (!filesManager.checkOrCreateDirectory(versionCacheDir)) {
                throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                        versionCacheDir.getAbsolutePath()));
            }

            File[] files = versionCacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.exists()) {
                        try {
                            filesManager.recursiveDelete(file);
                        } catch (IOException e) {
                            throw new PluginException("Unable to delete AndroidOpenCV " +
                                    "build-cache files.\n" +
                                    ExceptionUtils.getCauses(e,
                                            messages.getString("caused_by")), e);
                        }
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return "cleanAndroidOpenCVBuildCache";
    }

    @Override
    public String getDescription() {
        return "Cleans AndroidOpenCV build-cache folder in user home directory.";
    }

    @Override
    public String getGroup() {
        return "AndroidOpenCV";
    }

}
