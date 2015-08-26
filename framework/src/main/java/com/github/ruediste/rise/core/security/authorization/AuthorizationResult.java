package com.github.ruediste.rise.core.security.authorization;

import java.util.List;

public class AuthorizationResult {

    final private boolean isAuthorized;
    final private List<AuthorizationFailure> authorizationFailures;

    private AuthorizationResult(boolean isAuthorized,
            List<AuthorizationFailure> authorizationFailures) {
        this.isAuthorized = isAuthorized;
        this.authorizationFailures = authorizationFailures;
    }

    public static AuthorizationResult authorized() {
        return new AuthorizationResult(true, null);
    }

    public static AuthorizationResult failure(
            List<AuthorizationFailure> failures) {
        return new AuthorizationResult(false, failures);
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public List<AuthorizationFailure> getAuthorizationFailures() {
        return authorizationFailures;
    }

}
