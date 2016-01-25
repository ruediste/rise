package com.github.ruediste.rise.core.security;

import java.util.Optional;
import java.util.function.Supplier;

import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;
import com.github.ruediste.salta.jsr330.ImplementedBy;

/**
 * Holder of the current {@link AuthenticationSuccess}, if any.
 */
@ImplementedBy(DefaultAuthenticationHolder.class)
public interface AuthenticationHolder {
    /**
     * Get the current principal. Throws a {@link NoAuthenticationException} if
     * no principal is currently active.
     */
    Principal getCurrentPrincipal();

    Optional<Principal> tryGetCurrentPrincipal();

    /**
     * Get the current authentication. Throws a
     * {@link NoAuthenticationException} if no subject is logged in.
     */
    AuthenticationSuccess getCurrentAuthentication();

    Optional<AuthenticationSuccess> tryGetCurrentAuthentication();

    /**
     * Check if an authentication is present. Throws a
     * {@link NoAuthenticationException} if no subject is logged in.
     */
    void checkAutheticationPresetn();

    /**
     * Run code with a given subject
     */
    void withAuthentication(AuthenticationSuccess subject, Runnable action);

    /**
     * Run code with a given subject
     */
    <T> T withAuthentication(AuthenticationSuccess subject, Supplier<T> action);
}
