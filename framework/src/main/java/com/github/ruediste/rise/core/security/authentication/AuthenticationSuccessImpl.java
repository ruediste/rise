package com.github.ruediste.rise.core.security.authentication;

import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeAwareAuthenticationSuccess;

public class AuthenticationSuccessImpl
        implements RememberMeAwareAuthenticationSuccess {
    final private Principal principal;
    final private boolean suceededThroughRememberMe;

    public AuthenticationSuccessImpl(Principal principal,
            boolean suceededThroughRememberMe) {
        this.principal = principal;
        this.suceededThroughRememberMe = suceededThroughRememberMe;
    }

    @Override
    public Principal getPrincipal() {
        return principal;
    }

    @Override
    public boolean isSucceededThroughRememberMe() {
        return suceededThroughRememberMe;
    }
}
