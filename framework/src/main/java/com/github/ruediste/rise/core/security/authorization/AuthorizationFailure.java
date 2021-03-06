package com.github.ruediste.rise.core.security.authorization;

/**
 * Base classes describing authorization failures
 * 
 * <img src="doc-files/authorizationDecisionOverview.png" alt="">
 */
public class AuthorizationFailure {
    private final String message;

    public AuthorizationFailure(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + message + ")";
    }
}
