package com.github.ruediste.rise.core.security.authentication;

import com.github.ruediste.rise.core.security.Principal;

public class AuthenticationSuccess {
    final private Principal principal;

    public AuthenticationSuccess(Principal principal) {
        this.principal = principal;
    }

    public Principal getPrincipal() {
        return principal;
    }

}
