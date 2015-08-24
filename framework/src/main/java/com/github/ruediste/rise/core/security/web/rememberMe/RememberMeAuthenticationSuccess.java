package com.github.ruediste.rise.core.security.web.rememberMe;

import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.AuthenticationSuccess;

public class RememberMeAuthenticationSuccess extends AuthenticationSuccess {
    public RememberMeAuthenticationSuccess(Principal principal) {
        super(principal);
    }
}
