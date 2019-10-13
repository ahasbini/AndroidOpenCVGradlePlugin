package com.ahasbini.tools.androidopencv;

import com.ahasbini.tools.androidopencv.logging.Logger;
import com.ahasbini.tools.androidopencv.service.AndroidBuildScriptModifier;
import com.ahasbini.tools.androidopencv.service.DownloadManager;
import com.ahasbini.tools.androidopencv.service.FilesManager;

import org.gradle.api.Action;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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

        logger.info("Found android gradle plugin");

        // Add the extension to the project and wait for afterEvaluate is called when configuration
        // phase is finished
        project.getExtensions().create("androidOpenCV", AndroidOpenCVExtension.class);
        project.afterEvaluate(new AfterEvaluateAction());

        // Modify the android configs for them to be picked up before being evaluated by the plugin
        AndroidBuildScriptModifier androidBuildScriptModifier =
                new AndroidBuildScriptModifier(project);
        try {
            androidBuildScriptModifier.modifyAndroidBuildScript();
        } catch (Exception e) {
            throw new PluginException("Couldn't modify the android block to include Android OpenCV " +
                    "libs" + ".\n" +
                    "Caused by: " + e.getLocalizedMessage(), e);
        }
    }

    private class AfterEvaluateAction implements Action<Project> {

        @Override
        public void execute(Project project) {
            logger.debug("execute called");
            AndroidOpenCVExtension androidOpenCVExtension =
                    project.getExtensions().findByType(AndroidOpenCVExtension.class);

            logger.info("androidOpenCVExtension: {}", androidOpenCVExtension);

            if (androidOpenCVExtension == null || androidOpenCVExtension.getVersion() == null
                    || androidOpenCVExtension.getVersion().equals("")) {
                throw new PluginException(messages.getString("missing_opencv_version"));
            }

            String requestedVersion = androidOpenCVExtension.getVersion();
            logger.info("Requested OpenCV version: {}", requestedVersion);

            FilesManager filesManager = new FilesManager(project);
            DownloadManager downloadManager = new DownloadManager(project);

            // Check the user profile android opencv cache for existing version or perform download
            File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                    ".androidopencv");
            if (filesManager.checkOrCreateDirectory(androidOpenCVCacheDir)) {
                throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                        androidOpenCVCacheDir.getAbsolutePath()));
            }

            File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);
            if (filesManager.checkOrCreateDirectory(versionCacheDir)) {
                throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                        versionCacheDir.getAbsolutePath()));
            }

            File[] cacheFiles = versionCacheDir.listFiles();
            if (cacheFiles == null || cacheFiles.length == 0) {
                // Download the needed files
                logger.info("Downloading needed files for {} in {}", requestedVersion,
                        versionCacheDir.getAbsolutePath());
                try {
                    // TODO: 12-Oct-19 ahasbini: verify this with functional test
                    downloadManager.download(requestedVersion, versionCacheDir);
                    cacheFiles = versionCacheDir.listFiles();

                    if (cacheFiles == null || cacheFiles.length == 0) {
                        throw new IllegalStateException("Download was completed but files were not " +
                                "found in destination path");
                    }
                } catch (Exception e) {
                    // TODO: 12-Oct-19 ahasbini: externalize message
                    throw new PluginException("Unable to download " + requestedVersion + ".\n" +
                            "Caused by: " + e.getLocalizedMessage(), e);
                }
            }

            logger.info("Files in cache:");
            for (File cacheFile : cacheFiles) {
                logger.info("\t{}", cacheFile.getAbsolutePath());
            }

            File[] zips = versionCacheDir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("zip");
                }
            });

            if (zips == null || zips.length != 1) {
                // TODO: 12-Oct-19 ahasbini: externalize message
                throw new PluginException("Could not find downloaded zip file");
            }

            File androidOpenCVRequestedZipFile = zips[0];
            logger.info("Zip file: {}", androidOpenCVRequestedZipFile.getAbsolutePath());

            File androidOpenCVExtractedZipDir = new File(versionCacheDir,
                    androidOpenCVRequestedZipFile.getName().replace(".zip", ""));
            if (filesManager.checkOrCreateDirectory(androidOpenCVExtractedZipDir)) {
                throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                        androidOpenCVExtractedZipDir.getAbsolutePath()));
            }

            File[] androidOpenCVExtractedFiles = androidOpenCVExtractedZipDir.listFiles();
            if (androidOpenCVExtractedFiles == null || androidOpenCVExtractedFiles.length == 0) {
                // Unzip the file if the extracted files were not found
                try {
                    filesManager.unzipFile(androidOpenCVRequestedZipFile, androidOpenCVExtractedZipDir);
                    androidOpenCVExtractedFiles = androidOpenCVExtractedZipDir.listFiles();

                    if (androidOpenCVExtractedFiles == null ||
                            androidOpenCVExtractedFiles.length == 0) {
                        throw new IllegalStateException("Extracting zip file completed but files " +
                                "were not found in destination path");
                    }
                } catch (IOException e) {
                    // TODO: 12-Oct-19 ahasbini: externalize message
                    throw new PluginException("Android Gradle Plugin was unable to process zip file: " +
                            androidOpenCVRequestedZipFile.getAbsolutePath() + ".\n" +
                            "Caused by: " + e.getLocalizedMessage(), e);
                }
            }

            logger.info("Extracted zip files:");
            for (File androidOpenCVExtractedFile : androidOpenCVExtractedFiles) {
                logger.info("\t{}", androidOpenCVExtractedFile.getAbsolutePath());
            }

            // TODO: 12-Oct-19 ahasbini: implement verification of files using SHA1

            // Create the project android opencv dir
            File androidOpenCVBuildDir = new File(project.getBuildDir(), "androidopencv");
            if (filesManager.checkOrCreateDirectory(androidOpenCVBuildDir)) {
                throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                        androidOpenCVBuildDir.getAbsolutePath()));
            }

            File[] androidOpenCVBuildFiles = androidOpenCVBuildDir.listFiles();
            if (androidOpenCVBuildFiles == null || androidOpenCVBuildFiles.length == 0) {
                // Copy the needed files into the project android opencv dir
                if (androidOpenCVExtractedFiles.length == 1 &&
                        androidOpenCVExtractedFiles[0].getName().endsWith("OpenCV-android-sdk") &&
                        androidOpenCVExtractedFiles[0].isDirectory()) {
                    try {
                        filesManager.recursiveCopy(
                                new File(androidOpenCVExtractedFiles[0], "sdk"),
                                new File(androidOpenCVBuildDir, "sdk"), new FilenameFilter() {

                                    @Override
                                    public boolean accept(File dir, String name) {
                                        return !(name.contains("AndroidManifest.xml") ||
                                                name.contains("build.gradle") ||
                                                name.contains("libcxx_helper"));
                                    }
                                });
                        androidOpenCVBuildFiles = androidOpenCVBuildDir.listFiles();

                        if (androidOpenCVBuildFiles == null ||
                                androidOpenCVBuildFiles.length == 0) {
                            throw new IllegalStateException("Copying files completed but files " +
                                    "were not found in destination path");
                        }

                    } catch (IOException e) {
                        throw new PluginException("Unable to copy downloaded files to project " +
                                "build directory.\n" +
                                "Caused by: " + e.getLocalizedMessage(), e);
                    }
                } else {
                    // TODO: 14-Oct-19 ahasbini: externalize message
                    throw new PluginException("Failed to find the files needed for copying");
                }
            }

            logger.info("Files in build dir:");
            for (File androidOpenCVBuildFile : androidOpenCVBuildFiles) {
                logger.info("\t{}", androidOpenCVBuildFile.getAbsolutePath());
            }

            logger.info("execute finished");
        }
    }
}
