package com.github.ruediste.rise.core.security.authentication;

import com.github.ruediste.rise.core.security.authentication.core.AuthenticationFailure;

public class UserNameNotFoundAuthenticationFailure
        extends AuthenticationFailure {

    final private String userName;

    public UserNameNotFoundAuthenticationFailure(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

}
