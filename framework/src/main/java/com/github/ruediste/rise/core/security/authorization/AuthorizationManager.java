package com.github.ruediste.rise.core.security.authorization;

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
 * This is mainly a holder for a {@link RightsChecker}, providing some
 * convenience methods. Since the combination of different authorization
 * mechanisms is very application dependent, only a single {@link RightsChecker}
 * is referenced. Create a custom implementation to combine different sources.
 */
@Singleton
public class AuthorizationManager {

    @Inject
    AuthenticationHolder authenticationHolder;

    @FunctionalInterface
    public interface RightsChecker {
        /**
         * Check if the provided authentication implies all specified rights.
         * 
         * @param authentication
         *            the authentication to check against, or
         *            {@link Optional#empty()} if no authentication is present
         *            (anonymous user, guest)
         */
        AuthorizationResult performAuthorization(Set<? extends Object> rights,
                Optional<AuthenticationSuccess> authentication);
    }

    private RightsChecker rightsChecker;

    public void checkAuthorization(Set<? extends Object> rights,
            Optional<AuthenticationSuccess> authentication) {
        performAuthorization(rights, authentication).checkAuthorized();
    }

    /**
     * Check if the provided authentication implies all specified rights
     * 
     * @param authentication
     *            the authentication to check against, or
     *            {@link Optional#empty()} if no authentication is present
     *            (anonymous user, guest)
     */
    public AuthorizationResult performAuthorization(
            Set<? extends Object> rights,
            Optional<AuthenticationSuccess> authentication) {
        return rightsChecker.performAuthorization(rights, authentication);
    }

    public void checkAuthorization(Set<? extends Object> rights) {
        performAuthorization(rights).checkAuthorized();
    }

    /**
     * Check if the current authentication implies all specified rights
     */
    public AuthorizationResult performAuthorization(
            Set<? extends Object> rights) {
        return performAuthorization(rights,
                authenticationHolder.tryGetCurrentAuthentication());
    }

    public RightsChecker getRightsChecker() {
        return rightsChecker;
    }

    public void setRightsChecker(RightsChecker rightsChecker) {
        this.rightsChecker = rightsChecker;
    }
}
