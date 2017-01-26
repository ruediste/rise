package com.github.ruediste.rise.core.security.authentication;

import com.github.ruediste.rise.core.security.authentication.core.AuthenticationProvider;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationResult;

public class AuthenticationProviderKnownPrincipal
        implements AuthenticationProvider<AuthenticationRequestKnownPrincipal> {

    @Override
    public AuthenticationResult authenticate(AuthenticationRequestKnownPrincipal request) {
        return AuthenticationResult.success(new AuthenticationSuccessImpl(request.getPrincipal()));
    }

}
