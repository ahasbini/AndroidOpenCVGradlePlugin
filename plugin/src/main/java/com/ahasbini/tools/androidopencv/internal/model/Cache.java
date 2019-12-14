package com.ahasbini.tools.androidopencv.internal.model;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by ahasbini on 12-Dec-19.
 */
public class Cache implements Serializable {

    private LinkedHashMap<String, Object> inputProperties;
    private LinkedHashMap<File, Long> inputFiles;
    private LinkedHashMap<File, Long> outputFiles;

    public Cache(LinkedHashMap<String, Object> inputProperties,
                 LinkedHashMap<File, Long> inputFiles, LinkedHashMap<File, Long> outputFiles) {
        this.inputProperties = inputProperties;
        this.inputFiles = inputFiles;
        this.outputFiles = outputFiles;
    }

    public LinkedHashMap<String, Object> getInputProperties() {
        return inputProperties;
    }

    public LinkedHashMap<File, Long> getInputFiles() {
        return inputFiles;
    }

    public LinkedHashMap<File, Long> getOutputFiles() {
        return outputFiles;
    }

    @Override
    public String toString() {
        return "Cache{" +
                "inputProperties=" + inputProperties +
                ", inputFiles=" + inputFiles +
                ", outputFiles=" + outputFiles +
                '}';
    }
}
