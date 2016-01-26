package com.github.ruediste.rise.core.security.web;

import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;
import com.github.ruediste.salta.jsr330.ImplementedBy;

/**
 * Holder of the current user login
 */
@ImplementedBy(SessionLoginHolder.class)
public interface LoginHolder {

    /**
     * Set the {@link AuthenticationSuccess} corresponding to the current login.
     * Set to null to remove the login
     */
    void setSuccess(AuthenticationSuccess success);

    AuthenticationSuccess getSuccess();

}
