package com.github.ruediste.rise.core.security.web;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;
import com.github.ruediste.rise.util.GenericEvent;
import com.github.ruediste.rise.util.GenericEventManager;

/**
 * Manager for login/logout
 */
@Singleton
public class LoginManager {

    @Inject
    Logger log;

    @Inject
    LoginHolder loginHolder;

    private GenericEventManager<AuthenticationSuccess> loginEvent = new GenericEventManager<>();
    private GenericEventManager<AuthenticationSuccess> logoutEvent = new GenericEventManager<>();

    /**
     * Login the current user with the given success
     */
    public void login(AuthenticationSuccess success) {
        log.debug("logging in {}", success);
        loginHolder.setSuccess(success);
        loginEvent.fire(success);
    }

    /**
     * Event raised when a user is logged in.
     */
    public GenericEvent<AuthenticationSuccess> loginEvent() {
        return loginEvent.event();
    }

    /**
     * Logout the currently logged in user. Calls when no user is logged in are
     * ignored.
     */
    public void logout() {
        AuthenticationSuccess success = loginHolder.getSuccess();
        if (success == null)
            return;
        log.debug("logging out {}", success);
        loginHolder.setSuccess(null);
        logoutEvent.fire(success);
    }

    public GenericEvent<AuthenticationSuccess> logoutEvent() {
        return logoutEvent.event();
    }
}
