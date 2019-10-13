package com.ahasbini.tools.androidopencv.service;

import com.ahasbini.tools.androidopencv.logging.Logger;

import org.gradle.api.Project;

import java.io.File;
import java.util.ResourceBundle;

import de.undercouch.gradle.tasks.download.DownloadAction;

/**
 * Created by ahasbini on 12-Oct-19.
 */
public class DownloadManager {
    // TODO: 12-Oct-19 ahasbini: implement testing for this

    private final Logger logger = Logger.getLogger(DownloadManager.class);
    private final ResourceBundle messages = ResourceBundle.getBundle("messages");

    private Project project;

    public DownloadManager(Project project) {
        this.project = project;
    }

    public void download(String requestedVersion, File versionCacheDir) throws Exception {
        DownloadAction downloadAction = new DownloadAction(project);
        downloadAction.src(getResolvedUrl(requestedVersion));
        downloadAction.dest(versionCacheDir);
        downloadAction.overwrite(true);
        logger.quiet(messages.getString("downloading_android_opencv"));
        downloadAction.execute();
    }

    private String getResolvedUrl(String version) {
        // TODO: 12-Oct-19 ahasbini: cover other possibly malformed url such as: 
        // TODO: 12-Oct-19 ahasbini: https://sourceforge.net/projects/opencvlibrary/files/opencv-android/2.4.13/opencv-2.4.13.2-android-sdk.zip
        return "https://sourceforge.net/projects/opencvlibrary/files/" + version + "/opencv-" +
                version +"-android-sdk.zip";
    }
}
