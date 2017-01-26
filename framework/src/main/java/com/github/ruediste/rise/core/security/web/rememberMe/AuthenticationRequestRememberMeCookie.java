package com.github.ruediste.rise.core.security.web.rememberMe;

import java.security.AuthProvider;

import com.github.ruediste.rise.core.security.authentication.core.AuthenticationRequest;

/**
 * Request to perform an authentication based on a remember me cookie contained
 * in the current request.
 * 
 * <p>
 * The cookie is not included since since it has to be set by the
 * {@link AuthProvider} as well. This keeps all cookie handling in one place
 * (the provider)
 */
public class AuthenticationRequestRememberMeCookie implements AuthenticationRequest {

}
