package com.github.ruediste.rise.core.security.web.rememberMe;

import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;

/**
 * Interface for {@link AuthenticationSuccess}es which are aware of the concept
 * of a "rememeber-me" functionality.
 */
public interface RememberMeAwareAuthenticationSuccess
        extends AuthenticationSuccess {

    /**
     * return true if this authentication success succeeded using some
     * remeber-me technique.
     */
    boolean isSucceededThroughRememberMe();
}
