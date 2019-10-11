package com.ahasbini.tools.androidopencv;

/**
 * Created by ahasbini on 10-Oct-19.
 */
public class AndroidOpenCVExtension {


    private String version = null;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void version(String version) {
        this.version = version;
    }

    public void version(int version) {
        this.version = version + "";
    }

    @Override
    public String toString() {
        return "AndroidCVExtension{" +
                "version='" + version + '\'' +
                '}';
    }
}
