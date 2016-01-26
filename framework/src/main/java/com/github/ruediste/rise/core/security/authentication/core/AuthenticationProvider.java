package com.github.ruediste.rise.core.security.authentication.core;

/**
 * Performs authentication of a certain type of {@link AuthenticationRequest}.
 * Typically registered with {@link DefaultAuthenticationManager}
 */
public interface AuthenticationProvider<T extends AuthenticationRequest> {
    /**
     * Authenticate an authentication request.
     */
    AuthenticationResult authenticate(T request);

    /**
     * Invoked after the provider has been registered with the
     * {@link AuthenticationManager}
     */
    default void initialize(AuthenticationManager mgr) {
    }
}
