package com.github.ruediste.rise.core.security.authorization;

import java.util.Arrays;
import java.util.List;

import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;

/**
 * The outcome of checking if an {@link AuthenticationSuccess} implies a set of
 * rights
 * 
 * <p>
 * <img src="doc-files/authorizationDecisionOverview.png" alt="">
 */
public class AuthorizationResult {

    final private static AuthorizationResult authorized = new AuthorizationResult(true, null);
    final private boolean isAuthorized;
    final private List<AuthorizationFailure> authorizationFailures;

    private AuthorizationResult(boolean isAuthorized, List<AuthorizationFailure> authorizationFailures) {
        this.isAuthorized = isAuthorized;
        this.authorizationFailures = authorizationFailures;
    }

    public static AuthorizationResult authorized() {
        return authorized;
    }

    public static AuthorizationResult failure(AuthorizationFailure... failures) {
        return failure(Arrays.asList(failures));
    }

    public static AuthorizationResult failure(List<AuthorizationFailure> failures) {
        return new AuthorizationResult(false, failures);
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public List<AuthorizationFailure> getAuthorizationFailures() {
        return authorizationFailures;
    }

    public void checkAuthorized() {
        if (!isAuthorized)
            throw new AuthorizationException(authorizationFailures);
    }

}
