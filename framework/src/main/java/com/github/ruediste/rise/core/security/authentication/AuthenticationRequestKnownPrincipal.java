package com.github.ruediste.rise.core.security.authentication;

import com.github.ruediste.rise.core.security.Principal;

/**
 * Request for an authentication (login) using username/password
 */
public class AuthenticationRequestKnownPrincipal implements AuthenticationRequestRememberMeAware {
    private Principal principal;
    private boolean rememberMe;

    public AuthenticationRequestKnownPrincipal() {
    }

    public AuthenticationRequestKnownPrincipal(Principal principal) {
        this.principal = principal;
    }

    @Override
    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }
}
