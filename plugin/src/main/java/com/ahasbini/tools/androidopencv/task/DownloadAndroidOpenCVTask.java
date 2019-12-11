package com.ahasbini.tools.androidopencv.task;

import com.ahasbini.tools.androidopencv.AndroidOpenCVExtension;
import com.ahasbini.tools.androidopencv.Constants;
import com.ahasbini.tools.androidopencv.PluginException;
import com.ahasbini.tools.androidopencv.internal.service.DownloadManager;
import com.ahasbini.tools.androidopencv.internal.service.FilesManager;
import com.ahasbini.tools.androidopencv.internal.util.ExceptionUtils;
import com.ahasbini.tools.androidopencv.internal.util.Logger;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Created by ahasbini on 27-Nov-19.
 */
// TODO: 11-Dec-19 ahasbini: implement with caching mechanism
@CacheableTask
public class DownloadAndroidOpenCVTask extends DefaultTask {

    private final Logger logger = Logger.getLogger(DownloadAndroidOpenCVTask.class);
    private final ResourceBundle messages = ResourceBundle.getBundle("messages");

    @Input
    public String getVersion() {
        logger.debug("getVersion called");

        return getProject().getExtensions().getByType(AndroidOpenCVExtension.class).getVersion();
    }

    @Input
    public String getUrl() {
        logger.debug("getUrl called");

        return getProject().getExtensions().getByType(AndroidOpenCVExtension.class).getUrl();
    }

    @OutputFile
    public File getDestinationFile() {
        logger.debug("getDestinationFile called");

        String requestedVersion = getVersion();

        File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                ".androidopencv");

        File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);

        return new File(versionCacheDir, String.format(
                Constants.OPENCV_VERSION_ANDROID_SDK_ZIP_FILE_NAME, requestedVersion));
    }

    @TaskAction
    public void downloadAndroidOpenCV() {
        logger.debug("downloadAndroidOpenCV called");

        FilesManager filesManager = new FilesManager(getProject());
        DownloadManager downloadManager = new DownloadManager(getProject());
        AndroidOpenCVExtension androidOpenCVExtension = getProject().getExtensions()
                .getByType(AndroidOpenCVExtension.class);
        String requestedVersion = androidOpenCVExtension.getVersion();
        File[] cacheFiles;

        // TODO: 14-Oct-19 ahasbini: create a test with different user home location
        File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                ".androidopencv");
        if (!filesManager.checkOrCreateDirectory(androidOpenCVCacheDir)) {
            throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                    androidOpenCVCacheDir.getAbsolutePath()));
        }

        File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);
        if (!filesManager.checkOrCreateDirectory(versionCacheDir)) {
            throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                    versionCacheDir.getAbsolutePath()));
        }

        // Download the needed files
        logger.info("Downloading needed files for {} in {}", requestedVersion,
                versionCacheDir.getAbsolutePath());
        try {
            // TODO: 12-Oct-19 ahasbini: verify this with functional test
            String url = "https://sourceforge.net/projects/opencvlibrary/files/" + requestedVersion +
                    "/opencv-" + requestedVersion + "-android-sdk.zip";

            if (androidOpenCVExtension.getUrl() != null && !androidOpenCVExtension.getUrl().equals("")) {
                url = androidOpenCVExtension.getUrl();
            }

            downloadManager.download(url, new File(versionCacheDir,
                    String.format(Constants.OPENCV_VERSION_ANDROID_SDK_ZIP_FILE_NAME,
                            requestedVersion)));
            cacheFiles = versionCacheDir.listFiles((dir, name) -> name.endsWith("zip"));

            if (cacheFiles == null || cacheFiles.length == 0) {
                throw new PluginException("Download was completed but files were not " +
                        "found in destination path");
            }
        } catch (Exception e) {
            // TODO: 12-Oct-19 ahasbini: externalize message
            throw new PluginException("Unable to download " + requestedVersion + ".\n" +
                    ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
        }

        logger.info("Files in cache:");
        for (File cacheFile : cacheFiles) {
            logger.info("\t{}", cacheFile.getAbsolutePath());
        }
    }
}
