package com.github.ruediste.rise.core.security.authorization;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.core.front.RestartableApplicationBase;
import com.github.ruediste.rise.core.security.AuthenticationHolder;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;

/**
 * Manager to determine if an {@link AuthenticationSuccess} implies specified
 * rights.
 * 
 * <p>
 * Usually, application code uses {@link Authz} instead.
 * 
 * <p>
 * This is mainly a holder for a {@link AuthorizationDecisionPerformer},
 * providing some convenience methods. Since the combination of different
 * authorization mechanisms is very application dependent, only a single
 * {@link AuthorizationDecisionPerformer} is referenced. Create a custom
 * implementation to combine different sources. The performer is typically
 * initialized by the {@link RestartableApplicationBase}
 * 
 * <p>
 * The class contains corresponding check... and perform... methods. The
 * check... methods throw an exception if the authorization cannot be granted,
 * the perform... methods return an {@link AuthorizationResult}.
 * 
 * <p>
 * <img src="doc-files/authorizationDecisionOverview.png" alt="">
 */
@Singleton
public class AuthorizationDecisionManager {

    @Inject
    AuthenticationHolder authenticationHolder;

    @FunctionalInterface
    public interface AuthorizationDecisionPerformer {
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

    private AuthorizationDecisionPerformer authorizationPerformer;

    public void checkAuthorization(Right right, Optional<AuthenticationSuccess> authentication) {
        checkAuthorization(Collections.singleton(right), authentication);
    }

    public void checkAuthorization(Set<? extends Right> rights, Optional<AuthenticationSuccess> authentication) {
        performAuthorization(rights, authentication).checkAuthorized();
    }

    public AuthorizationResult performAuthorization(Right right, Optional<AuthenticationSuccess> authentication) {
        return performAuthorization(Collections.singleton(right), authentication);
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
        return authorizationPerformer.performAuthorization(rights, authentication);
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
    public AuthorizationResult performAuthorization(Set<? extends Right> rights) {
        return performAuthorization(rights, authenticationHolder.tryGetCurrentAuthentication());
    }

    public AuthorizationDecisionPerformer getAuthorizationPerformer() {
        return authorizationPerformer;
    }

    public void setPerformer(AuthorizationDecisionPerformer rightsChecker) {
        this.authorizationPerformer = rightsChecker;
    }
}
