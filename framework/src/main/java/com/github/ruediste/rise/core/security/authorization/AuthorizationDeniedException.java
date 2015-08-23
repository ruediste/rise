package com.github.ruediste.rise.core.security.authorization;

/**
 * Used when an {@link AuthorizationRequest} is denied.
 */
public class AuthorizationDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AuthorizationDeniedException(AuthorizationRequest<?, ?, ?> request) {
        super("Authorization denied: " + request);
    }
}
