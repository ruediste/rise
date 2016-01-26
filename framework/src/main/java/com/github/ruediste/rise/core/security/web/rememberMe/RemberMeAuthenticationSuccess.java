package com.github.ruediste.rise.core.security.web.rememberMe;

import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.AuthenticationSuccessImpl;

public class RemberMeAuthenticationSuccess extends AuthenticationSuccessImpl
        implements RememberMeAwareAuthenticationSuccess {

    protected RemberMeAuthenticationSuccess(Principal principal) {
        super(principal);

    }

    @Override
    public boolean isSucceededThroughRememberMe() {
        return true;
    }

}
