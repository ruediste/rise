package com.github.ruediste.rise.core.security.web.rememberMe;

import com.github.ruediste.rise.core.security.Subject;
import com.github.ruediste.rise.core.security.authentication.AuthenticationFailure;

public class RememberMeTokenTheftFailure extends AuthenticationFailure {

    final private Subject subject;

    public RememberMeTokenTheftFailure(Subject subject) {
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

}
