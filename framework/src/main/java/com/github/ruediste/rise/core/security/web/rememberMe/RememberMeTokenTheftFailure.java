package com.github.ruediste.rise.core.security.web.rememberMe;

import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationFailure;

public class RememberMeTokenTheftFailure extends AuthenticationFailure {

    final private Principal principal;

    public RememberMeTokenTheftFailure(Principal principal) {
        this.principal = principal;
    }

    public Principal getPrincipal() {
        return principal;
    }

}
