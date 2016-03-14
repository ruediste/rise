package com.github.ruediste.rise.core.security.authentication;

import com.github.ruediste.rise.core.security.authentication.core.AuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationRequest;

/**
 * Idicates that the password passed in an {@link AuthenticationRequest} did not
 * match the expected password.
 */
public class PasswordMismatchAuthenticationFailure extends AuthenticationFailure {

}
