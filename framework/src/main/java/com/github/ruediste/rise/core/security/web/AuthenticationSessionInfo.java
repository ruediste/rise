package com.github.ruediste.rise.core.security.web;

import com.github.ruediste.rise.core.scopes.SessionScoped;
import com.github.ruediste.rise.core.security.authentication.AuthenticationSuccess;

@SessionScoped
public class AuthenticationSessionInfo {

    private AuthenticationSuccess success;

    public AuthenticationSuccess getSuccess() {
        return success;
    }

    public void setSuccess(AuthenticationSuccess success) {
        this.success = success;
    }
}
