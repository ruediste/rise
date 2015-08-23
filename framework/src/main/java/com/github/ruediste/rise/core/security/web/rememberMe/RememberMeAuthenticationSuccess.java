package com.github.ruediste.rise.core.security.web.rememberMe;

import com.github.ruediste.rise.core.security.Subject;
import com.github.ruediste.rise.core.security.authentication.AuthenticationSuccess;

public class RememberMeAuthenticationSuccess extends AuthenticationSuccess {
    public RememberMeAuthenticationSuccess(Subject subject) {
        super(subject);
    }
}
