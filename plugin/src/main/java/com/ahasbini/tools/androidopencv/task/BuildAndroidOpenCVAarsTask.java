package com.ahasbini.tools.androidopencv.task;

import com.ahasbini.tools.androidopencv.AndroidOpenCVExtension;
import com.ahasbini.tools.androidopencv.Constants;
import com.ahasbini.tools.androidopencv.PluginException;
import com.ahasbini.tools.androidopencv.internal.service.FilesManager;
import com.ahasbini.tools.androidopencv.internal.service.Injector;
import com.ahasbini.tools.androidopencv.internal.util.ExceptionUtils;
import com.ahasbini.tools.androidopencv.internal.util.Logger;

import org.gradle.api.DefaultTask;
import org.gradle.api.NonNullApi;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by ahasbini on 27-Nov-19.
 */
// TODO: 11-Dec-19 ahasbini: implement with caching mechanism
@CacheableTask
@NonNullApi
public class BuildAndroidOpenCVAarsTask extends DefaultTask {

    private final Logger logger = Logger.getLogger(BuildAndroidOpenCVAarsTask.class);
    private final ResourceBundle messages = Injector.getMessages();

    @InputDirectory
    public File getInputFiles() {
        logger.debug("getInputFiles called");

        AndroidOpenCVExtension androidOpenCVExtension = getProject().getExtensions()
                .getByType(AndroidOpenCVExtension.class);
        String requestedVersion = androidOpenCVExtension.getVersion();

        File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                ".androidopencv");

        File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);

        File androidOpenCVExtractedZipDir = new File(versionCacheDir,
                String.format(Constants.OPENCV_VERSION_ANDROID_SDK_EXTRACTED_DIRECTORY_NAME,
                        requestedVersion));

        File androidOpenCVRootDir = new File(androidOpenCVExtractedZipDir,
                Constants.EXTRACTED_OPENCV_ROOT_DIRECTORY_NAME);

        return new File(androidOpenCVRootDir, "sdk");
    }

    @SuppressWarnings("unused")
    @OutputFiles
    public File[] getOutputFiles() {
        logger.debug("getOutputFiles called");

        AndroidOpenCVExtension androidOpenCVExtension = getProject().getExtensions()
                .getByType(AndroidOpenCVExtension.class);
        String requestedVersion = androidOpenCVExtension.getVersion();

        File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                ".androidopencv");

        File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);

        File androidOpenCVBuildCacheDir = new File(versionCacheDir, "build-cache");

        File androidOpenCVBuildCacheOutputsDir = new File(androidOpenCVBuildCacheDir,
                "outputs");

        return new File[]{
                new File(androidOpenCVBuildCacheOutputsDir, "" +
                        Constants.OPENCV_AAR_NAME_PREFIX + "-debug-" + requestedVersion + ".aar"),
                new File(androidOpenCVBuildCacheOutputsDir, "" +
                        Constants.OPENCV_AAR_NAME_PREFIX + "-release-" + requestedVersion + ".aar")
        };
    }

    @TaskAction
    public void buildAndroidOpenCVAars() {
        logger.debug("buildAndroidOpenCVAars called");

        performBuildAndroidOpenCVAars();
    }

    private void performBuildAndroidOpenCVAars() {
        logger.debug("performBuildAndroidOpenCVAars called");

        FilesManager filesManager = Injector.getFilesManager(getProject());
        AndroidOpenCVExtension androidOpenCVExtension = getProject().getExtensions()
                .getByType(AndroidOpenCVExtension.class);
        String requestedVersion = androidOpenCVExtension.getVersion();

        File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                ".androidopencv");
        if (!filesManager.checkDirectory(androidOpenCVCacheDir)) {
            throw new PluginException(String.format(messages.getString("cannot_find_dir"),
                    androidOpenCVCacheDir.getAbsolutePath()));
        }

        File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);
        if (!filesManager.checkDirectory(versionCacheDir)) {
            throw new PluginException(String.format(messages.getString("cannot_find_dir"),
                    versionCacheDir.getAbsolutePath()));
        }

        File androidOpenCVExtractedZipDir = new File(versionCacheDir,
                String.format(Constants.OPENCV_VERSION_ANDROID_SDK_EXTRACTED_DIRECTORY_NAME,
                        requestedVersion));
        if (!filesManager.checkDirectory(androidOpenCVExtractedZipDir)) {
            throw new PluginException(String.format(messages.getString("cannot_find_dir"),
                    androidOpenCVExtractedZipDir.getAbsolutePath()));
        }

        File androidOpenCVRootDir = new File(androidOpenCVExtractedZipDir,
                Constants.EXTRACTED_OPENCV_ROOT_DIRECTORY_NAME);
        if (!filesManager.checkDirectory(androidOpenCVRootDir)) {
            throw new PluginException(String.format(messages.getString("cannot_find_dir"),
                    androidOpenCVExtractedZipDir.getAbsoluteFile()));
        }

        // Build AAR binaries in user cache dir using Gradle Tooling API
        logger.info("Compiling OpenCV aar binaries");

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

        // Create gradle.properties file with needed variables
        try {
            File androidOpenCVBuildGradlePropFile = new File(androidOpenCVBuildCacheDir,
                    "gradle.properties");
            if (!filesManager.checkOrCreateFile(androidOpenCVBuildGradlePropFile)) {
                throw new IOException("Unable to create 'gradle.properties' file");
            }

            char[] versionCodeOriginal =
                    requestedVersion.replace(".", "").toCharArray();
            char[] versionCodeFinal = {'0', '0', '0', '0'};
            System.arraycopy(versionCodeOriginal, 0, versionCodeFinal, 0,
                    versionCodeOriginal.length);

            ArrayList<String> lines = new ArrayList<>();
            lines.add("opencv_dir=" +
                    androidOpenCVRootDir.getAbsolutePath().replace("\\", "\\\\"));
            lines.add("opencv_version_name=" + requestedVersion);
            lines.add("opencv_version_code=" + new String(versionCodeFinal));
            lines.add("opencv_aar_name_prefix=" + Constants.OPENCV_AAR_NAME_PREFIX);

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

            File androidOpenCVBuildCacheDebugAar = new File(androidOpenCVBuildCacheOutputsDir,
                    "" + Constants.OPENCV_AAR_NAME_PREFIX + "-debug-" + requestedVersion +
                            ".aar");

            File androidOpenCVBuildCacheReleaseAar = new File(androidOpenCVBuildCacheOutputsDir,
                    "" + Constants.OPENCV_AAR_NAME_PREFIX + "-release-" + requestedVersion +
                            ".aar");

            if (!filesManager.checkFile(androidOpenCVBuildCacheDebugAar) ||
                    !filesManager.checkFile(androidOpenCVBuildCacheReleaseAar)) {
                throw new PluginException("Binaries were compiled but " +
                        "files were not found in destination path");
            }
        } catch (Exception e) {
            throw new PluginException("Unable to compile binaries.\n" +
                    ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
        }
    }
}
