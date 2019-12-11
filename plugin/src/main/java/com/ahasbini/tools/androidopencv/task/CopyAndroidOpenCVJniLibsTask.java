package com.ahasbini.tools.androidopencv.task;

import com.ahasbini.tools.androidopencv.AndroidOpenCVExtension;
import com.ahasbini.tools.androidopencv.Constants;
import com.ahasbini.tools.androidopencv.internal.util.Logger;

import org.gradle.api.tasks.Copy;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Created by ahasbini on 27-Nov-19.
 */
// TODO: 10-Dec-19 ahasbini: checks after copying is complete for error handling
// TODO: 10-Dec-19 ahasbini: test incremental build with inputs and outputs
public class CopyAndroidOpenCVJniLibsTask extends Copy {

    private final Logger logger = Logger.getLogger(CopyAndroidOpenCVJniLibsTask.class);
    private final ResourceBundle messages = ResourceBundle.getBundle("messages");

    // TODO: 10-Dec-19 ahasbini: move checks with exceptions to a doFirst block

    public CopyAndroidOpenCVJniLibsTask() {
        getProject().afterEvaluate(project -> {
            logger.debug("execute called");

            /*FilesManager filesManager = new FilesManager(project);*/
            AndroidOpenCVExtension androidOpenCVExtension =
                    project.getExtensions().getByType(AndroidOpenCVExtension.class);
            String requestedVersion = androidOpenCVExtension.getVersion();

            File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                    ".androidopencv");
            /*if (!filesManager.checkDirectory(androidOpenCVCacheDir)) {
                throw new PluginException(String.format(
                        messages.getString("cannot_find_dir"),
                        androidOpenCVCacheDir.getAbsolutePath()));
            }*/

            File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);
            /*if (!filesManager.checkDirectory(versionCacheDir)) {
                throw new PluginException(String.format(
                        messages.getString("cannot_find_dir"),
                        versionCacheDir.getAbsolutePath()));
            }*/

            File androidOpenCVExtractedZipDir = new File(versionCacheDir,
                    String.format(Constants.OPENCV_VERSION_ANDROID_SDK_EXTRACTED_DIRECTORY_NAME,
                            requestedVersion));
            /*if (!filesManager.checkDirectory(androidOpenCVExtractedZipDir)) {
                throw new PluginException(String.format(
                        messages.getString("cannot_find_dir"),
                        androidOpenCVExtractedZipDir.getAbsolutePath()));
            }*/

            File androidOpenCVRootDir = new File(androidOpenCVExtractedZipDir,
                    Constants.EXTRACTED_OPENCV_ROOT_DIRECTORY_NAME);
            /*if (!filesManager.checkDirectory(androidOpenCVRootDir)) {
                throw new PluginException(String.format(
                        "Could not find extracted zip files in folder %s",
                        androidOpenCVExtractedZipDir.getAbsoluteFile()));
            }*/

            File androidOpenCVProjectBuildDir = new File(project.getBuildDir(),
                    "androidopencv");
            /*if (!filesManager.checkOrCreateDirectory(androidOpenCVProjectBuildDir)) {
                throw new PluginException(String.format(messages.getString("cannot_create_dir"),
                        androidOpenCVProjectBuildDir.getAbsolutePath()));
            }*/

            from(androidOpenCVRootDir);
            include("sdk/native/**");
            into(androidOpenCVProjectBuildDir);
        });
    }
}
