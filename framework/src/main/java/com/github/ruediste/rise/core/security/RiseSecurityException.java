package com.github.ruediste.rise.core.security;

/**
 * Indicates a security related exception, either
 */
public class RiseSecurityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RiseSecurityException() {
    }

    public RiseSecurityException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RiseSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public RiseSecurityException(String message) {
        super(message);
    }

    public RiseSecurityException(Throwable cause) {
        super(cause);
    }

}
