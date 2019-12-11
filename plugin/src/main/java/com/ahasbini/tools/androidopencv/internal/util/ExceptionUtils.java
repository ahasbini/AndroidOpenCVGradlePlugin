package com.ahasbini.tools.androidopencv.internal.util;

/**
 * Created by ahasbini on 02-Nov-19.
 */
public class ExceptionUtils {

    public static String getCauses(Exception e, String format) {
        StringBuilder causes = new StringBuilder();

        causes.append(String.format(format, e.toString()));
        Throwable t = e.getCause();
        while (t != null) {
            causes.append(String.format(format, t.toString()));
            t = t.getCause();
        }

        return causes.toString();
    }

}
