package com.ahasbini.tools.androidopencv.internal.service;

import com.ahasbini.tools.androidopencv.internal.util.Logger;

import org.gradle.api.Project;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ahasbini on 12-Oct-19.
 */
public class FilesManager {
    // TODO: 12-Oct-19 ahasbini: implement testing for this

    private final Logger logger = Logger.getLogger(FilesManager.class);

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private Project project;

    FilesManager(Project project) {
        this.project = project;
    }

    public void unzipFile(File zipFile, File targetDir) throws IOException {
        logger.info("Unzipping {} into {}", zipFile.getAbsolutePath(), targetDir.getAbsolutePath());
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        unzipRecursively(zipInputStream, zipEntry, targetDir);
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean checkDirectory(File directory) {
        return directory.exists() && directory.isDirectory();
    }

    public void recursiveCopy(File src, File dst, FilenameFilter filter) throws IOException {
        if (src.isDirectory()) {
            if (dst.exists()) {
                recursiveDelete(dst);
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

    public void recursiveDelete(File path) throws IOException {
        Files.walkFileTree(path.toPath(), new SimpleFileVisitor<Path>() {

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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean checkFile(File file) {
        return file.exists() && file.isFile();
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

    public Map<String, String> writeAndGetMd5Sums(File md5File, File path,
                                                  boolean performIntegrityCheck,
                                                  FilenameFilter filenameFilter)
            throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        LinkedHashMap<String, String> fileMd5SumLinkedHashMap = new LinkedHashMap<>();
        MessageDigest md = MessageDigest.getInstance("MD5");

        SimpleFileVisitor<Path> fileSimpleFileVisitor = new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                if (filenameFilter == null ||
                        filenameFilter.accept(file.toFile().getParentFile(),
                                file.toFile().getName())) {
                    String checksum = computeHash(file.toFile(), md);
                    fileMd5SumLinkedHashMap.put(path.toPath().relativize(file).toString(),
                            checksum);
                }
                return super.visitFile(file, attrs);
            }
        };

        if (md5File.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(md5File)) {
                try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                    //noinspection unchecked
                    LinkedHashMap<String, String> tempLinkedHashMap =
                            (LinkedHashMap<String, String>) objectInputStream.readObject();
                    fileMd5SumLinkedHashMap.putAll(tempLinkedHashMap);
                }
            } catch (EOFException e) {
                // md5 file is corrupted or empty
                Files.walkFileTree(path.toPath(), fileSimpleFileVisitor);

                //noinspection ResultOfMethodCallIgnored
                md5File.delete();
            }
        } else {
            Files.walkFileTree(path.toPath(), fileSimpleFileVisitor);
        }

        if (md5File.exists() && performIntegrityCheck) {
            LinkedHashMap<String, String> pathMd5SumLinkedHashMap = new LinkedHashMap<>();

            Files.walkFileTree(path.toPath(), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    if (filenameFilter == null ||
                            filenameFilter.accept(file.toFile().getParentFile(),
                            file.toFile().getName())) {
                        String checksum = computeHash(file.toFile(), md);
                        pathMd5SumLinkedHashMap.put(path.toPath().relativize(file).toString(),
                                checksum);
                    }
                    return super.visitFile(file, attrs);
                }
            });

            if (!pathMd5SumLinkedHashMap.equals(fileMd5SumLinkedHashMap)) {
                return new LinkedHashMap<>();
            }
        }

        if (!md5File.exists()) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(md5File)) {
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                    objectOutputStream.writeObject(fileMd5SumLinkedHashMap);
                }
            }
        }

        return fileMd5SumLinkedHashMap;
    }

    private String computeHash(File file, MessageDigest md) throws IOException {
        // DigestInputStream is better, but you also can hash file like this.
        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int nread;
            while ((nread = fis.read(buffer)) != -1) {
                md.update(buffer, 0, nread);
            }
        }

        // bytes to hex
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public long computeCRC(File file) throws IOException {
        CRC32 crc = new CRC32();
        try (FileInputStream fis = new FileInputStream(file)) {
            try (BufferedInputStream bis = new BufferedInputStream(fis)) {
                byte[] bytes = new byte[1024];
                int i;
                while ((i = bis.read(bytes)) != -1) {
                    crc.update(bytes, 0, i);
                }
            }
        }
        return crc.getValue();
    }
}
