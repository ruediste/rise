package com.github.ruediste.rise.core.security.authorization;

import java.util.List;

/**
 * Exception representing a failed authorization
 * <p>
 * <img src="doc-files/authorizationDecisionOverview.png" alt="">
 */
public class AuthorizationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final List<AuthorizationFailure> authorizationFailures;

    public AuthorizationException(List<AuthorizationFailure> authorizationFailures) {
        this.authorizationFailures = authorizationFailures;
    }

    @Override
    public String getMessage() {
        return getClass().getSimpleName() + "(" + authorizationFailures + ")";
    }

    public AuthorizationResult toAuthorizationResult() {
        return AuthorizationResult.failure(authorizationFailures);
    }

}
