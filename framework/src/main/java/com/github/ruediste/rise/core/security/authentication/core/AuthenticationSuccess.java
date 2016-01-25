package com.github.ruediste.rise.core.security.authentication.core;

import com.github.ruediste.rise.core.security.Principal;

/**
 * Represents a successful authentication.
 * 
 * <p>
 * In addition, subclasses can be used to capture additional information about
 * how the authentication succeeded. The canonical example is the remember-me
 * cookie.
 * 
 * <p>
 * <img src="doc-files/authenticationCoreOverview.png" alt="">
 */
public interface AuthenticationSuccess {

    Principal getPrincipal();

}
