package com.ahasbini.tools.androidopencv.service;

import com.ahasbini.tools.androidopencv.util.Logger;

import org.gradle.api.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean checkOrCreateDirectory(File directory) {
        return (directory.exists() && directory.isDirectory()) || directory.mkdirs();
    }

    public void recursiveCopy(File src, File dst, FilenameFilter filter) throws IOException {
        if (src.isDirectory()) {
            if (dst.exists()) {
                Files.walkFileTree(dst.toPath(), new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        if (new File(file.toUri()).delete()) {
                            return super.visitFile(file, attrs);
                        } else {
                            throw new IOException("Couldn't delete file: " + file.toString());
                        }
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (new File(dir.toUri()).delete()) {
                            return super.postVisitDirectory(dir, exc);
                        } else {
                            throw new IOException("Couldn't delete directory: " + dir.toString());
                        }
                    }
                });
            }
            //noinspection ResultOfMethodCallIgnored
            dst.delete();
            Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
            File[] files = src.listFiles(filter);
            if (files != null) {
                for (File file : files) {
                    recursiveCopy(file, new File(dst, file.getName()), filter);
                }
            }
        } else {
            Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public boolean checkOrCreateFile(File file) throws IOException {
        return (file.exists() && !file.isDirectory()) || file.createNewFile();
    }

    public void writeFolderContentsFromClasspath(String classpath, File directory)
            throws URISyntaxException, IOException {
        URL resource = getClass().getResource(classpath);
        File classpathDir = new File(resource.toURI());
        File[] files = classpathDir.listFiles();
        if (files != null) {
            for (File file : files) {
                recursiveCopy(file, new File(directory, file.getName()), null);
            }
        }
    }
}
