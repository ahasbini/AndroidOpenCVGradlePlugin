package com.ahasbini.tools.androidopencv.internal.service;

import com.ahasbini.tools.androidopencv.AndroidOpenCVExtension;
import com.ahasbini.tools.androidopencv.internal.model.Cache;

import org.gradle.api.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ahasbini on 11-Dec-19.
 */
public class CacheManager {

    private final Project project;
    private final FilesManager filesManager;
    private final String requestedVersion;
    private final LinkedHashMap<String, Cache> cacheLinkedHashMap = new LinkedHashMap<>();

    private boolean modified = false;

    CacheManager(Project project) {
        this.project = project;
        this.filesManager = Injector.getFilesManager(project);
        this.requestedVersion = project.getExtensions().getByType(AndroidOpenCVExtension.class).getVersion();

        File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                ".androidopencv");

        File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);

        File cacheFile = new File(versionCacheDir, ".cache");

        if (cacheFile.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(cacheFile)) {
                try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                    //noinspection unchecked
                    LinkedHashMap<String, Cache> tempLinkedHashMap =
                            (LinkedHashMap<String, Cache>) objectInputStream.readObject();
                    cacheLinkedHashMap.putAll(tempLinkedHashMap);
                }
            } catch (IOException | ClassNotFoundException e) {
                //noinspection ResultOfMethodCallIgnored
                cacheFile.delete();
            }
        }
    }

    public void saveCache() throws IOException {
        File androidOpenCVCacheDir = new File(System.getProperty("user.home"),
                ".androidopencv");

        File versionCacheDir = new File(androidOpenCVCacheDir, requestedVersion);

        File cacheFile = new File(versionCacheDir, ".cache");


        try (FileOutputStream fileOutputStream = new FileOutputStream(cacheFile)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(cacheLinkedHashMap);
            }
        }
    }

    public boolean checkCacheForTask(Class taskClass, Map<String, Object> inputProperties,
                                     Set<File> inputFiles, Set<File> outputFiles) {
        Cache cache = cacheLinkedHashMap.get(taskClass.getName());
        if (cache == null) {
            return false;
        }

        if (!cache.getInputProperties().isEmpty() || !inputProperties.isEmpty()) {
            if (!inputProperties.equals(cache.getInputProperties())) {
                return false;
            }
        }

        LinkedHashMap<File, Long> cacheInputFilesHashMap = cache.getInputFiles();
        Set<File> cacheInputFiles = cacheInputFilesHashMap.keySet();
        if (!cacheInputFiles.isEmpty() || !inputFiles.isEmpty()) {
            if (!cacheInputFiles.equals(inputFiles)) {
                return false;
            }
        }
        LinkedHashMap<File, Long> inputFilesHashMap = new LinkedHashMap<>();
        for (File inputFile : inputFiles) {
            // TODO: 14-Dec-19 ahasbini: implement CRC32 for input files
        }



        LinkedHashMap<File, Long> cacheOutputFilesHashMap = cache.getOutputFiles();
        Set<File> cacheOutputFiles = cacheOutputFilesHashMap.keySet();
        if (!cacheOutputFiles.isEmpty() || !outputFiles.isEmpty()) {
            if (!cacheOutputFiles.equals(outputFiles)) {
                return false;
            }
        }
        LinkedHashMap<File, Long> outputFilesHashMap = new LinkedHashMap<>();
        for (File outputFile : outputFiles) {
            // TODO: 14-Dec-19 ahasbini: implement CRC32 for output files
        }

        return true;
    }

    public void putCacheForTask(Class taskClass, Map<String, Object> properties,
                                Set<File> inputFiles, Set<File> outputFiles) {
        // TODO: 14-Dec-19 ahasbini: implement

        modified = true;
    }

    public boolean isCacheModified() {
        return modified;
    }
}
