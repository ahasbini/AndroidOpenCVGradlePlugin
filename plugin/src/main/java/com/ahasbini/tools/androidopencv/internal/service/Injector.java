package com.ahasbini.tools.androidopencv.internal.service;

import com.ahasbini.tools.androidopencv.internal.util.Logger;

import org.gradle.api.Project;

import java.util.ResourceBundle;

/**
 * Created by ahasbini on 11-Dec-19.
 */
public class Injector {

    private static final Logger logger = Logger.getLogger(Injector.class);

    private static Project project;
    private static DownloadManager downloadManager;
    private static FilesManager filesManager;
    private static CacheManager cacheManager;
    private static AndroidBuildScriptModifier androidBuildScriptModifier;
    private static ResourceBundle messages;

    public static void init() {
        project = null;
        downloadManager = null;
        filesManager = null;
        cacheManager = null;
        androidBuildScriptModifier = null;
        messages = null;
    }

    public static DownloadManager getDownloadManager(Project project) {
        logger.debug("getDownloadManager called");
        logger.debug("project " + Integer.toHexString(project.hashCode()));

        if (Injector.project != null && !project.equals(Injector.project)) {
            logger.warn("received a different project! hash codes: " +
                    Integer.toHexString(project.hashCode()) + " != " +
                    Integer.toHexString(Injector.project.hashCode()));
        }

        if (downloadManager == null) {
            Injector.project = project;
            downloadManager = new DownloadManager(project);
        }
        return downloadManager;
    }

    public static FilesManager getFilesManager(Project project) {
        logger.debug("getFilesManager called");
        logger.debug("project " + Integer.toHexString(project.hashCode()));

        if (Injector.project != null && !project.equals(Injector.project)) {
            logger.warn("received a different project! hash codes: " +
                    Integer.toHexString(project.hashCode()) + " != " +
                    Integer.toHexString(Injector.project.hashCode()));
        }

        if (filesManager == null) {
            filesManager = new FilesManager(project);
        }
        return filesManager;
    }

    public static CacheManager getCacheManager(Project project) {
        logger.debug("getCacheManager called");
        logger.debug("project " + Integer.toHexString(project.hashCode()));

        if (Injector.project != null && !project.equals(Injector.project)) {
            logger.warn("received a different project! hash codes: " +
                    Integer.toHexString(project.hashCode()) + " != " +
                    Integer.toHexString(Injector.project.hashCode()));
        }

        if (cacheManager == null) {
            cacheManager = new CacheManager(project);
        }
        return cacheManager;
    }

    public static AndroidBuildScriptModifier getAndroidBuildScriptModifier(Project project) {
        logger.debug("getAndroidBuildScriptModifier called");
        logger.debug("project " + Integer.toHexString(project.hashCode()));

        if (Injector.project != null && !project.equals(Injector.project)) {
            logger.warn("received a different project! hash codes: " +
                    Integer.toHexString(project.hashCode()) + " != " +
                    Integer.toHexString(Injector.project.hashCode()));
        }

        if (androidBuildScriptModifier == null) {
            androidBuildScriptModifier = new AndroidBuildScriptModifier(project);
        }
        return androidBuildScriptModifier;
    }

    public static ResourceBundle getMessages() {
        if (messages == null) {
            messages = ResourceBundle.getBundle("messages");
        }
        return messages;
    }
}
