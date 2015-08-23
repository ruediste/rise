package com.github.ruediste.rise.core.security.authentication;

/**
 * Performs authentication of a certain type of {@link AuthenticationRequest}.
 * Typically registered with {@link DefaultAuthenticationManager}
 */
public interface AuthenticationProvider<T extends AuthenticationRequest> {
    /**
     * Authenticate an authentication request.
     */
    AuthenticationResult tryAuthenticate(T request);
}
