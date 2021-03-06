package com.github.ruediste.rise.core.security.authentication.core;

import com.github.ruediste.rise.util.GenericEvent;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.ImplementedBy;

/**
 * Manages the authentication of {@link AuthenticationRequest}s.
 * 
 * <p>
 * <img src="doc-files/authenticationCoreOverview.png" alt="">
 */
@ImplementedBy(DefaultAuthenticationManager.class)
public interface AuthenticationManager {

    /**
     * Authenticate an authentication request. Throws an exception if the
     * authentication is not successful
     */
    AuthenticationResult authenticate(AuthenticationRequest request);

    /**
     * Raised before an authentication is attempted
     */
    GenericEvent<AuthenticationRequest> preAuthenticationEvent();

    /**
     * Raised after an authentication
     */
    GenericEvent<Pair<AuthenticationRequest, AuthenticationResult>> postAuthenticationEvent();

}