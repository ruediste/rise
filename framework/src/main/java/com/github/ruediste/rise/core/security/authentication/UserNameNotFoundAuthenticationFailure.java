package com.github.ruediste.rise.core.security.authentication;

public class UserNameNotFoundAuthenticationFailure
        extends AuthenticationFailure {

    final private String userName;

    protected UserNameNotFoundAuthenticationFailure(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

}
