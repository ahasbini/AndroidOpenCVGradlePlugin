package com.ahasbini.tools.androidopencv.internal.service;

import java.io.IOException;

/**
 * Created by ahasbini on 15-Dec-19.
 */
public class UnsupportedProtocolException extends IOException {

    public UnsupportedProtocolException() {
    }

    public UnsupportedProtocolException(String message) {
        super(message);
    }

    public UnsupportedProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedProtocolException(Throwable cause) {
        super(cause);
    }
}
