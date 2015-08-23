package com.github.ruediste.rise.core.security.authentication;

public class UsernamePasswordAuthenticationRequest implements
        AuthenticationRequest, RememberMeAwareAuthenticationRequest {

    private String userName;
    private String password;
    private boolean rememberMe;

    public UsernamePasswordAuthenticationRequest() {
    }

    public UsernamePasswordAuthenticationRequest(String userName,
            String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
