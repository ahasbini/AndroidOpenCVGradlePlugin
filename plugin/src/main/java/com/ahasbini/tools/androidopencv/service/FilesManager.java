package com.ahasbini.tools.androidopencv.service;

import com.ahasbini.tools.androidopencv.logging.Logger;

import org.gradle.api.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ahasbini on 12-Oct-19.
 */
public class FilesManager {
    // TODO: 12-Oct-19 ahasbini: implement testing for this

    private final Logger logger = Logger.getLogger(FilesManager.class);

    private Project project;

    public FilesManager(Project project) {
        this.project = project;
    }

    public void unzipFile(File zipFile, File targetDir) throws IOException {
        logger.info("Unzipping {} into {}", zipFile.getAbsolutePath(), targetDir.getAbsolutePath());
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        unzipRecursively(zipInputStream, zipEntry, targetDir);
        zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        zipEntry = zipInputStream.getNextEntry();
    }

    private void unzipRecursively(ZipInputStream zipInputStream, ZipEntry zipEntry, File target)
            throws IOException {
        if (zipEntry != null) {
            if (zipEntry.isDirectory()) {
                File file = new File(target, zipEntry.getName());
                if (file.mkdirs()) {
                    unzipRecursively(zipInputStream, zipInputStream.getNextEntry(), target);
                    //noinspection ResultOfMethodCallIgnored
                    file.setLastModified(zipEntry.getTime());
                } else {
                    throw new IOException("Couldn't create directory: " + file.getAbsolutePath());
                }
            } else {
                File file = new File(target, zipEntry.getName());
                Files.copy(zipInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                unzipRecursively(zipInputStream, zipInputStream.getNextEntry(), target);
                //noinspection ResultOfMethodCallIgnored
                file.setLastModified(zipEntry.getTime());
            }
        }
    }

    public boolean checkOrCreateDirectory(File directory) {
        return (!directory.exists() || !directory.isDirectory())
                && !directory.mkdirs();
    }

    public void recursiveCopy(File src, File dst,FilenameFilter filter) throws IOException {
        Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
        if (src.isDirectory()) {
            File[] files = src.listFiles(filter);
            if (files != null) {
                for (File file : files) {
                    recursiveCopy(file, new File(dst, file.getName()), filter);
                }
            }
        }
    }
}
