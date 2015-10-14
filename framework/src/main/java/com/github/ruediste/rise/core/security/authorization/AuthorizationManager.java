package com.github.ruediste.rise.core.security.authorization;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.core.security.AuthenticationHolder;
import com.github.ruediste.rise.core.security.authentication.AuthenticationSuccess;

/**
 * Manager to determine if an {@link AuthenticationSuccess} implies specified
 * rights.
 * 
 * <p>
 * This is mainly a holder for a {@link AuthorizationPerformer}, providing some
 * convenience methods. Since the combination of different authorization
 * mechanisms is very application dependent, only a single
 * {@link AuthorizationPerformer} is referenced. Create a custom implementation
 * to combine different sources.
 */
@Singleton
public class AuthorizationManager {

    @Inject
    AuthenticationHolder authenticationHolder;

    @FunctionalInterface
    public interface AuthorizationPerformer {
        /**
         * Check if the provided authentication implies all specified rights.
         * 
         * @param authentication
         *            the authentication to check against, or
         *            {@link Optional#empty()} if no authentication is present
         *            (anonymous user, guest)
         */
        AuthorizationResult performAuthorization(Set<? extends Right> rights,
                Optional<AuthenticationSuccess> authentication);
    }

    private AuthorizationPerformer authorizationPerformer;

    public void checkAuthorization(Right right,
            Optional<AuthenticationSuccess> authentication) {
        checkAuthorization(Collections.singleton(right), authentication);
    }

    public void checkAuthorization(Set<? extends Right> rights,
            Optional<AuthenticationSuccess> authentication) {
        performAuthorization(rights, authentication).checkAuthorized();
    }

    public AuthorizationResult performAuthorization(Right right,
            Optional<AuthenticationSuccess> authentication) {
        return performAuthorization(Collections.singleton(right),
                authentication);
    }

    /**
     * Check if the provided authentication implies all specified rights
     * 
     * @param authentication
     *            the authentication to check against, or
     *            {@link Optional#empty()} if no authentication is present
     *            (anonymous user, guest)
     */
    public AuthorizationResult performAuthorization(Set<? extends Right> rights,
            Optional<AuthenticationSuccess> authentication) {
        if (authorizationPerformer == null)
            return AuthorizationResult.authorized();
        return authorizationPerformer.performAuthorization(rights,
                authentication);
    }

    public void checkAuthorization(Right right) {
        checkAuthorization(Collections.singleton(right));
    }

    public void checkAuthorization(Set<? extends Right> rights) {
        performAuthorization(rights).checkAuthorized();
    }

    public AuthorizationResult performAuthorization(Right right) {
        return performAuthorization(Collections.singleton(right));
    }

    /**
     * Check if the current authentication implies all specified rights
     */
    public AuthorizationResult performAuthorization(
            Set<? extends Right> rights) {
        return performAuthorization(rights,
                authenticationHolder.tryGetCurrentAuthentication());
    }

    public AuthorizationPerformer getAuthorizationPerformer() {
        return authorizationPerformer;
    }

    public void setAuthorizationPerformer(
            AuthorizationPerformer rightsChecker) {
        this.authorizationPerformer = rightsChecker;
    }
}
