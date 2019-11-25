package com.ahasbini.tools.androidopencv;

import com.ahasbini.tools.androidopencv.service.AndroidBuildScriptModifier;
import com.ahasbini.tools.androidopencv.service.DownloadManager;
import com.ahasbini.tools.androidopencv.service.FilesManager;
import com.ahasbini.tools.androidopencv.util.ExceptionUtils;
import com.ahasbini.tools.androidopencv.util.Logger;

import org.gradle.api.Action;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
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

        String enableAndroidOpencvLogs = project.getGradle().getStartParameter()
                .getProjectProperties().get("ENABLE_ANDROID_OPENCV_LOGS");
        if (enableAndroidOpencvLogs != null) {
            logger.quiet("AndroidOpenCVGradlePlugin logs enabled");
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

        logger.info("AndroidOpenCVGradlePlugin version " + BuildConfig.APP_VERSION);
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
                    "libs.\n" +
                    ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
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
            AndroidBuildScriptModifier androidBuildScriptModifier =
                    new AndroidBuildScriptModifier(project);

            // TODO: 26-Nov-19 ahasbini: START download
            // Check the user profile android opencv cache for existing version or perform download
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
                        throw new PluginException("Download was completed but files were not " +
                                "found in destination path");
                    }
                } catch (Exception e) {
                    // TODO: 12-Oct-19 ahasbini: externalize message
                    throw new PluginException("Unable to download " + requestedVersion + ".\n" +
                            ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
                }
            }

            logger.info("Files in cache:");
            for (File cacheFile : cacheFiles) {
                logger.info("\t{}", cacheFile.getAbsolutePath());
            }
            // TODO: 26-Nov-19 ahasbini: FINISH download

            // TODO: 26-Nov-19 ahasbini: START unzip
            File[] zips = versionCacheDir.listFiles(
                    (dir, name) -> name.endsWith("zip"));

            if (zips == null || zips.length != 1) {
                // TODO: 12-Oct-19 ahasbini: externalize message
                throw new PluginException("Could not find downloaded zip file");
            }

            File androidOpenCVRequestedZipFile = zips[0];
            logger.info("Zip file: {}", androidOpenCVRequestedZipFile.getAbsolutePath());

            File androidOpenCVExtractedZipDir = new File(versionCacheDir,
                    androidOpenCVRequestedZipFile.getName().replace(".zip", ""));
            if (!filesManager.checkOrCreateDirectory(androidOpenCVExtractedZipDir)) {
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
                        throw new PluginException("Extracting zip file completed but files " +
                                "were not found in destination path");
                    }
                } catch (IOException e) {
                    // TODO: 12-Oct-19 ahasbini: externalize message
                    throw new PluginException("Android Gradle Plugin was unable to process zip file: " +
                            androidOpenCVRequestedZipFile.getAbsolutePath() + ".\n" +
                            ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
                }
            }

            logger.info("Extracted zip files:");
            for (File androidOpenCVExtractedFile : androidOpenCVExtractedFiles) {
                logger.info("\t{}", androidOpenCVExtractedFile.getAbsolutePath());
            }
            // TODO: 26-Nov-19 ahasbini: FINISH unzip

            // TODO: 26-Nov-19 ahasbini: START copy JNI libs
            // Create the project android opencv dir
            File androidOpenCVProjectBuildDir = new File(project.getBuildDir(),
                    "androidopencv");
            if (!filesManager.checkOrCreateDirectory(androidOpenCVProjectBuildDir)) {
                throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                        androidOpenCVProjectBuildDir.getAbsolutePath()));
            }

            FilenameFilter sdkNativeFilenameFilter = (dir, name) ->
                    (name.equals("native") && dir.getPath().endsWith("sdk")) ||
                            dir.getPath().matches(".*sdk" +
                                    File.separator.replace("\\",
                                            "\\\\") +
                                    "native.*");

            // Perform copying and/or verification
            File[] androidOpenCVProjectBuildFiles = androidOpenCVProjectBuildDir.listFiles();
            if (androidOpenCVProjectBuildFiles == null || androidOpenCVProjectBuildFiles.length == 0) {
                // Copy the needed files into the project android opencv dir
                if (androidOpenCVExtractedFiles.length == 1 &&
                        androidOpenCVExtractedFiles[0].getName().endsWith("OpenCV-android-sdk") &&
                        androidOpenCVExtractedFiles[0].isDirectory()) {
                    try {
                        filesManager.recursiveCopy(
                                new File(androidOpenCVExtractedFiles[0], "sdk"),
                                new File(androidOpenCVProjectBuildDir, "sdk"),
                                sdkNativeFilenameFilter);
                        androidOpenCVProjectBuildFiles = androidOpenCVProjectBuildDir.listFiles();

                        if (androidOpenCVProjectBuildFiles == null ||
                                androidOpenCVProjectBuildFiles.length == 0) {
                            throw new PluginException("Copying files completed but files " +
                                    "were not found in destination path");
                        }
                    } catch (IOException e) {
                        throw new PluginException("Unable to copy downloaded files to project " +
                                "build directory.\n" +
                                ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
                    }

                    try {
                        // Perform verification setup
                        Map<String, String> sourceMd5Map = filesManager.writeAndGetMd5Sums(
                                new File(versionCacheDir,
                                        requestedVersion + ".md5map"),
                                new File(androidOpenCVExtractedFiles[0], "sdk"),
                                false, sdkNativeFilenameFilter);
                        Map<String, String> destinationMd5Map = filesManager.writeAndGetMd5Sums(
                                new File(androidOpenCVProjectBuildDir,
                                        requestedVersion + ".md5map"),
                                new File(androidOpenCVProjectBuildDir, "sdk"),
                                false, sdkNativeFilenameFilter);

                        if (!sourceMd5Map.equals(destinationMd5Map)) {
                            throw new PluginException("Failed to setup MD5 for verification");
                        }
                    } catch (IOException | NoSuchAlgorithmException | ClassNotFoundException e) {
                        throw new PluginException("Unable to perform MD5 verification.\n" +
                                ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
                    }
                } else {
                    // TODO: 14-Oct-19 ahasbini: externalize message
                    throw new PluginException("Failed to find the files needed for copying");
                }
            } else {
                if (androidOpenCVExtractedFiles.length == 1 &&
                        androidOpenCVExtractedFiles[0].getName().endsWith("OpenCV-android-sdk") &&
                        androidOpenCVExtractedFiles[0].isDirectory()) {
                    try {
                        // Perform verification
                        Map<String, String> sourceMd5Map = filesManager.writeAndGetMd5Sums(
                                new File(versionCacheDir,
                                        requestedVersion + ".md5map"),
                                new File(androidOpenCVExtractedFiles[0], "sdk"),
                                true, sdkNativeFilenameFilter);
                        Map<String, String> destinationMd5Map = filesManager.writeAndGetMd5Sums(
                                new File(androidOpenCVProjectBuildDir,
                                        requestedVersion + ".md5map"),
                                new File(androidOpenCVProjectBuildDir, "sdk"),
                                true, sdkNativeFilenameFilter);

                        if (!sourceMd5Map.equals(destinationMd5Map)) {
                            // TODO: 23-Nov-19 ahasbini: need clean task in this scenario
                            throw new PluginException("MD5 verification failed, destination may be corrupted");
                        }
                    } catch (IOException | NoSuchAlgorithmException | ClassNotFoundException e) {
                        throw new PluginException("Unable to perform md5 verification.\n" +
                                ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
                    }
                } else {
                    // TODO: 14-Oct-19 ahasbini: externalize message
                    throw new PluginException("Failed to find the files needed for verification");
                }
            }

            logger.info("Files in build dir:");
            for (File androidOpenCVBuildFile : androidOpenCVProjectBuildFiles) {
                logger.info("\t{}", androidOpenCVBuildFile.getAbsolutePath());
            }
            // TODO: 26-Nov-19 ahasbini: FINISH copy JNI libs

            // TODO: 26-Nov-19 ahasbini: START build aars
            // Build AAR binaries in user cache dir using Gradle Tooling API
            File androidOpenCVBuildCacheDir = new File(versionCacheDir, "build-cache");
            if (!filesManager.checkOrCreateDirectory(androidOpenCVBuildCacheDir)) {
                throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                        androidOpenCVBuildCacheDir.getAbsolutePath()));
            }

            File androidOpenCVBuildCacheOutputsDir = new File(androidOpenCVBuildCacheDir,
                    "outputs");
            if (!filesManager.checkOrCreateDirectory(androidOpenCVBuildCacheOutputsDir)) {
                throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                        androidOpenCVBuildCacheOutputsDir.getAbsolutePath()));
            }

            File[] androidOpenCVBuildCacheFiles = androidOpenCVBuildCacheOutputsDir.listFiles(
                    (dir, name) -> name.endsWith(".aar"));
            if (androidOpenCVBuildCacheFiles == null || androidOpenCVBuildCacheFiles.length == 0) {

                logger.info("Compiling OpenCV aar binaries");

                // Create gradle.properties file with needed variables
                try {
                    File androidOpenCVBuildGradlePropFile = new File(androidOpenCVBuildCacheDir,
                            "gradle.properties");
                    if (!filesManager.checkOrCreateFile(androidOpenCVBuildGradlePropFile)) {
                        throw new IOException("Unable to create 'gradle.properties file");
                    }

                    char[] versionCodeOriginal =
                            requestedVersion.replace(".", "").toCharArray();
                    char[] versionCodeFinal = {'0', '0', '0', '0'};
                    System.arraycopy(versionCodeOriginal, 0, versionCodeFinal, 0,
                            versionCodeOriginal.length);

                    ArrayList<String> lines = new ArrayList<>();
                    lines.add("opencv_dir=" +
                            androidOpenCVExtractedFiles[0].getAbsolutePath()
                                    .replace("\\", "\\\\"));
                    lines.add("opencv_version_name=" + requestedVersion);
                    lines.add("opencv_version_code=" + new String(versionCodeFinal));

                    Files.write(androidOpenCVBuildGradlePropFile.toPath(), lines,
                            StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException e) {
                    throw new PluginException("Unable to create/write 'gradle.properties' file.\n" +
                            ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
                }

                // Copy build scripts from classpath
                try {
                    filesManager.writeFolderContentsFromClasspath("/build-cache-scripts",
                            androidOpenCVBuildCacheDir);
                } catch (Exception e) {
                    throw new PluginException("Unable to create/write build scripts.\n" +
                            ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
                }

                // Build AAR binaries
                try (ProjectConnection buildCacheProjectConnection = GradleConnector.newConnector()
                        .forProjectDirectory(androidOpenCVBuildCacheDir)
                        .useGradleVersion("4.1")
                        .connect()) {

                    BuildLauncher launcher = buildCacheProjectConnection.newBuild()
                            .forTasks(":compileOpenCV")
                            // TODO: 18-Oct-19 ahasbini: wrap around in logger or something else
                            .setStandardOutput(System.out)
                            .setStandardError(System.err);

                    launcher.run();

                    androidOpenCVBuildCacheFiles = androidOpenCVBuildCacheOutputsDir.listFiles(
                            (dir, name) -> name.endsWith(".aar"));

                    if (androidOpenCVBuildCacheFiles == null ||
                            androidOpenCVBuildCacheFiles.length == 0) {
                        throw new PluginException("Binaries wer compiled but " +
                                "files were not found in destination path");
                    }
                } catch (Exception e) {
                    throw new PluginException("Unable to compile binaries.\n" +
                            ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
                }
            }
            // TODO: 26-Nov-19 ahasbini: FINISH build aars

            // Add built AARs to project dependencies
            try {
                for (File androidOpenCVBuildCacheFile : androidOpenCVBuildCacheFiles) {
                    String configurationName =
                            androidOpenCVBuildCacheFile.getName().matches(".*-debug-.*") ?
                                    "debugImplementation" :
                                    (androidOpenCVBuildCacheFile.getName().matches(".*-release-.*") ?
                                            "releaseImplementation" : null);
                    androidBuildScriptModifier.addLibrary(
                            androidOpenCVBuildCacheFile.getParentFile(),
                            configurationName,
                            ":" + androidOpenCVBuildCacheFile.getName()
                                    .replace("-" + requestedVersion + ".",
                                            ":" + requestedVersion + "@"));
                }
            } catch (Exception e) {
                throw new PluginException("Unable to copy compiled binaries.\n" +
                        ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
            }

            logger.info("execute finished");
        }
    }
}
