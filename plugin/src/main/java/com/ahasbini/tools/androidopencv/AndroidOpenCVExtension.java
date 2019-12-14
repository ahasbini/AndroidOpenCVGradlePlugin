package com.ahasbini.tools.androidopencv;

/**
 * Created by ahasbini on 10-Oct-19.
 */
public class AndroidOpenCVExtension {

    // TODO: 11-Dec-19 ahasbini: remove url to prevent conflict with cache and make plugin prone to serious issues

    private String version = "";
    @Deprecated
    private String url = "";

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void version(String version) {
        this.version = version;
    }

    @Deprecated
    public String getUrl() {
        return url;
    }

    @Deprecated
    public void setUrl(String url) {
        this.url = url;
    }

    @Deprecated
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
