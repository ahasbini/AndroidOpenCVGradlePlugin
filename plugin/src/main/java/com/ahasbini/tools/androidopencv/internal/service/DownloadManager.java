package com.ahasbini.tools.androidopencv.internal.service;

import com.ahasbini.tools.androidopencv.internal.util.Logger;

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
    private final ResourceBundle messages = Injector.getMessages();

    private Project project;

    DownloadManager(Project project) {
        this.project = project;
    }

    public void download(String url, File dstFile) throws Exception {
        DownloadAction downloadAction = new DownloadAction(project);
        downloadAction.src(url);
        downloadAction.dest(dstFile);
        downloadAction.onlyIfModified(true);
        logger.quiet(messages.getString("downloading_android_opencv"));
        downloadAction.execute();
    }
}
