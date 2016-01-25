package com.github.ruediste.rise.core.security.authentication;

import com.github.ruediste.rise.core.security.authentication.core.AuthenticationRequest;

/**
 * Interface for {@link AuthenticationRequest}s which are aware of the existence
 * of a "Remember-Me" feature.
 */
public interface RememberMeAwareAuthenticationRequest
        extends AuthenticationRequest {

    /**
     * Used by the framework to determine if the user wishes to be remembered
     * (for example "remember me" check box ticked)
     * 
     * @return true to indicate that remembering is desired, false otherwise
     */
    boolean isRememberMe();
}
