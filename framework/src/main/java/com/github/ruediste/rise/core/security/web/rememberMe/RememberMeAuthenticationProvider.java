package com.github.ruediste.rise.core.security.web.rememberMe;

import java.security.SecureRandom;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.security.authentication.AuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.AuthenticationProvider;
import com.github.ruediste.rise.core.security.authentication.AuthenticationRequest;
import com.github.ruediste.rise.core.security.authentication.AuthenticationResult;
import com.github.ruediste.rise.core.security.authentication.RememberMeAwareAuthenticationRequest;
import com.google.common.base.Charsets;

public class RememberMeAuthenticationProvider implements
        AuthenticationProvider<RememberMeAuthenticationRequest> {

    @Inject
    CoreConfiguration config;

    @Inject
    CoreRequestInfo info;

    SecureRandom random = new SecureRandom();

    @PostConstruct
    public void postConstruct(AuthenticationManager authenticationManager) {
        authenticationManager.postAuthenticationEvent().addListener(
                pair -> {
                    AuthenticationRequest req = pair.getA();
                    AuthenticationResult res = pair.getB();
                    if (req instanceof RememberMeAwareAuthenticationRequest
                            && ((RememberMeAwareAuthenticationRequest) req)
                                    .isRememberMe() && res.isSuccess()) {
                        // set the remember me token with a new series
                        RememberMeToken token = createToken();
                        dao.newToken(token, res.getSuccess().getSubject());
                        info.getServletResponse().addCookie(
                                createRememberMeCookie(token));
                    }
                });
    }

    RememberMeTokenDao dao;

    @Override
    public AuthenticationResult tryAuthenticate(
            RememberMeAuthenticationRequest request) {
        // extract the token from the request
        Cookie cookie = getCookie(info.getServletRequest(),
                config.rememberMeCookieName);
        if (cookie != null) {
            RememberMeToken token = parseToken(cookie.getValue());
            RememberMeToken storedToken = dao.loadToken(token.getId());
            if (storedToken != null) {
                if (storedToken.getToken().equals(token.getToken())) {
                    // token matches, set new token, return success
                    random.nextBytes(token.getToken());
                    dao.updateToken(token);
                    info.getServletResponse().addCookie(
                            createRememberMeCookie(token));
                    return AuthenticationResult
                            .success(new RememberMeAuthenticationSuccess(dao
                                    .loadSubject(token.getId())));
                } else {
                    // series did match, but token did not. There appears to
                    // have been a token theft
                    return AuthenticationResult
                            .failure(new RememberMeTokenTheftFailure());
                }
            }
        }

        // no cookie present, or no token for the series found
        return AuthenticationResult
                .failure(new NoRememberMeTokenFoundAuthenticationFailure());
    }

    private RememberMeToken createToken() {
        RememberMeToken result = new RememberMeToken();
        result.token = new byte[20];
        result.series = new byte[20];
        random.nextBytes(result.getSeries());
        random.nextBytes(result.getToken());
        return result;
    }

    private Cookie createRememberMeCookie(RememberMeToken token) {

        String value = token.getId() + ","
                + Base64.getEncoder().encodeToString(token.getSeries()) + ","
                + Base64.getEncoder().encodeToString(token.getToken());
        Cookie result = new Cookie(config.rememberMeCookieName, value);
        result.setHttpOnly(true);
        return result;
    }

    private Cookie getCookie(HttpServletRequest request, String name) {
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    private RememberMeToken parseToken(String str) {
        String[] parts = str.split(";");
        RememberMeToken result = new RememberMeToken();
        result.id = Long.parseLong(new String(Base64.getDecoder().decode(
                parts[0]), Charsets.UTF_8));
        result.series = Base64.getDecoder().decode(parts[1]);
        result.token = Base64.getDecoder().decode(parts[2]);
        return result;
    }
}
