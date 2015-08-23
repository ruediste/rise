package com.github.ruediste.rise.core.security.authentication;

import com.github.ruediste.rise.core.security.Subject;

public class AuthenticationSuccess {
    final private Subject subject;

    public AuthenticationSuccess(Subject subject) {
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

}
