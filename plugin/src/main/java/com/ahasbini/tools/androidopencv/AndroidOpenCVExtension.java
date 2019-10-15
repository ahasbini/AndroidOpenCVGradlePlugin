package com.ahasbini.tools.androidopencv;

/**
 * Created by ahasbini on 10-Oct-19.
 */
public class AndroidOpenCVExtension {


    private String version = null;
    private String url = null;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void version(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void url(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "AndroidOpenCVExtension{" +
                "version='" + version + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
