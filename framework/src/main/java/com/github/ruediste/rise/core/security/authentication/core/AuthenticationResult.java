package com.github.ruediste.rise.core.security.authentication.core;

import java.util.Arrays;
import java.util.List;

import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.AuthenticationSuccessImpl;

/**
 * Result of an authentication attempt.
 * <p>
 * Can be a success or a failure. In case of failure, the
 * {@link AuthenticationFailure failures} are reported.
 * 
 * <p>
 * <img src="doc-files/authenticationCoreOverview.png" alt="">
 */
public class AuthenticationResult {
    final private boolean isSuccess;
    final private AuthenticationSuccess success;
    final private List<AuthenticationFailure> failures;

    protected AuthenticationResult(boolean isSuccess, AuthenticationSuccess success,
            List<AuthenticationFailure> failures) {
        this.isSuccess = isSuccess;
        this.success = success;
        this.failures = failures;
    }

    public static AuthenticationResult success(Principal principal) {
        return success(new AuthenticationSuccessImpl(principal));
    }

    public static AuthenticationResult success(AuthenticationSuccess success) {
        return new AuthenticationResult(true, success, null);
    }

    public static AuthenticationResult failure(AuthenticationFailure... failures) {
        return new AuthenticationResult(false, null, Arrays.asList(failures));
    }

    public static AuthenticationResult failure(List<AuthenticationFailure> failures) {
        return new AuthenticationResult(false, null, failures);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public AuthenticationSuccess getSuccess() {
        return success;
    }

    public List<AuthenticationFailure> getFailures() {
        return failures;
    }

}
