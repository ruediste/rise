package com.github.ruediste.rise.core.security.web;

import com.github.ruediste.rise.core.scopes.SessionScoped;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;

/**
 * Stores the {@link AuthenticationSuccess} (login) for the current session.
 */
@SessionScoped
public class SessionLoginHolder implements LoginHolder {

    private AuthenticationSuccess success;

    @Override
    public AuthenticationSuccess getSuccess() {
        return success;
    }

    /**
     * Set the successful authentication, or null to clear the current user
     */
    @Override
    public void setSuccess(AuthenticationSuccess success) {
        this.success = success;
    }
}
