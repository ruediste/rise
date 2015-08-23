package com.github.ruediste.rise.core.security.authentication;

import com.github.ruediste.rise.util.GenericEvent;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.ImplementedBy;

/**
 * Manages the authentication of {@link AuthenticationRequest}s
 */
@ImplementedBy(DefaultAuthenticationManager.class)
public interface AuthenticationManager {

    /**
     * Authenticate an authentication request. Throws an exception if the
     * authentication is not successful
     * 
     * @return
     */
    AuthenticationResult authenticate(AuthenticationRequest request);

    GenericEvent<AuthenticationRequest> preAuthenticationEvent();

    GenericEvent<Pair<AuthenticationRequest, AuthenticationResult>> postAuthenticationEvent();

}