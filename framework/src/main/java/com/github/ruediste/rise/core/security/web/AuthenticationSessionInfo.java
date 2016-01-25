package com.github.ruediste.rise.core.security.web;

import com.github.ruediste.rise.core.scopes.SessionScoped;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;

@SessionScoped
public class AuthenticationSessionInfo {

    private AuthenticationSuccess success;

    public AuthenticationSuccess getSuccess() {
        return success;
    }

    /**
     * Set the successful authentication, or null to clear the current user
     */
    public void setSuccess(AuthenticationSuccess success) {
        this.success = success;
    }
}
