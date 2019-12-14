package com.ahasbini.tools.androidopencv.task;

import com.ahasbini.tools.androidopencv.AndroidOpenCVExtension;
import com.ahasbini.tools.androidopencv.Constants;
import com.ahasbini.tools.androidopencv.internal.service.Injector;
import com.ahasbini.tools.androidopencv.internal.util.Logger;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Copy;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Created by ahasbini on 27-Nov-19.
 */
// TODO: 10-Dec-19 ahasbini: checks after unzipping is complete for error handling
// TODO: 10-Dec-19 ahasbini: test incremental build with inputs and outputs
// TODO: 11-Dec-19 ahasbini: implement with caching mechanism
@CacheableTask
public class UnZipAndroidOpenCVTask extends Copy {

    private final Logger logger = Logger.getLogger(UnZipAndroidOpenCVTask.class);
    private final ResourceBundle messages = Injector.getMessages();

    // TODO: 10-Dec-19 ahasbini: move checks with exceptions to a doFirst block

    public UnZipAndroidOpenCVTask() {
        getProject().afterEvaluate(project -> {
            logger.debug("execute called");

            /*FilesManager filesManager = new FilesManager(project);
            AndroidOpenCVExtension androidOpenCVExtension =
                    project.getExtensions().getByType(AndroidOpenCVExtension.class);
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

            File androidOpenCVRequestedZipFile = new File(versionCacheDir,
                    String.format(Constants.OPENCV_VERSION_ANDROID_SDK_ZIP_FILE_NAME,
                            requestedVersion));
            if (!filesManager.checkFile(androidOpenCVRequestedZipFile)) {
                // TODO: 12-Oct-19 ahasbini: externalize message
                throw new PluginException("Could not find downloaded zip file");
            }

            File androidOpenCVExtractedZipDir = new File(versionCacheDir,
                    String.format(Constants.OPENCV_VERSION_ANDROID_SDK_EXTRACTED_DIRECTORY_NAME,
                            requestedVersion));
            if (!filesManager.checkOrCreateDirectory(androidOpenCVExtractedZipDir)) {
                throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                        androidOpenCVExtractedZipDir.getAbsolutePath()));
            }*/

            from(project.zipTree(getInputZipFile()));
            into(getOutputZipDir());
        });
    }

    @SuppressWarnings("WeakerAccess")
    public File getInputZipFile() {
        logger.debug("getInputZipFile called");

        AndroidOpenCVExtension androidOpenCVExtension =
                getProject().getExtensions().getByType(AndroidOpenCVExtension.class);
        String requestedVersion = androidOpenCVExtension.getVersion();

        File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                ".androidopencv");

        File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);

        //noinspection UnnecessaryLocalVariable
        File androidOpenCVRequestedZipFile = new File(versionCacheDir,
                String.format(Constants.OPENCV_VERSION_ANDROID_SDK_ZIP_FILE_NAME,
                        requestedVersion));

        return androidOpenCVRequestedZipFile;
    }

    @SuppressWarnings("WeakerAccess")
    public File getOutputZipDir() {
        logger.debug("getOutputZipDir called");

        AndroidOpenCVExtension androidOpenCVExtension =
                getProject().getExtensions().getByType(AndroidOpenCVExtension.class);
        String requestedVersion = androidOpenCVExtension.getVersion();

        File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                ".androidopencv");

        File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);

        //noinspection UnnecessaryLocalVariable
        File androidOpenCVExtractedZipDir = new File(versionCacheDir,
                String.format(Constants.OPENCV_VERSION_ANDROID_SDK_EXTRACTED_DIRECTORY_NAME,
                        requestedVersion));

        return androidOpenCVExtractedZipDir;
    }

    @Override
    protected void copy() {
        logger.debug("copy called");

        super.copy();
    }
}
