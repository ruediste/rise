package com.github.ruediste.rise.core.security.authentication;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;

public class AuthenticationSuccessImpl extends AttachedPropertyBearerBase
        implements AuthenticationSuccess {
    final private Principal principal;

    public AuthenticationSuccessImpl(Principal principal) {
        this.principal = principal;
    }

    @Override
    public Principal getPrincipal() {
        return principal;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + principal + "]";
    }
}
