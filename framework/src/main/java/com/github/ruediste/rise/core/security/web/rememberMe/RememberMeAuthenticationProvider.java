package com.github.ruediste.rise.core.security.web.rememberMe;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.security.Subject;
import com.github.ruediste.rise.core.security.authentication.AuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.AuthenticationProvider;
import com.github.ruediste.rise.core.security.authentication.AuthenticationRequest;
import com.github.ruediste.rise.core.security.authentication.AuthenticationResult;
import com.github.ruediste.rise.core.security.authentication.RememberMeAwareAuthenticationRequest;
import com.google.common.base.Strings;

public class RememberMeAuthenticationProvider implements
        AuthenticationProvider<RememberMeAuthenticationRequest> {

    @Inject
    Logger log;

    @Inject
    CoreConfiguration config;

    @Inject
    CoreRequestInfo info;

    private SecureRandom random = new SecureRandom();

    private RememberMeTokenDao dao;

    @PostConstruct
    public void postConstruct(AuthenticationManager authenticationManager) {
        authenticationManager
                .postAuthenticationEvent()
                .addListener(
                        pair -> {
                            AuthenticationRequest req = pair.getA();
                            AuthenticationResult res = pair.getB();
                            if (req instanceof RememberMeAwareAuthenticationRequest
                                    && ((RememberMeAwareAuthenticationRequest) req)
                                            .isRememberMe() && res.isSuccess()) {
                                // set the remember me token with a new series
                                log.debug("Successful login with remember me set to true, adding cookie to the response");
                                RememberMeToken token = createToken();
                                dao.newToken(token, res.getSuccess()
                                        .getSubject());
                                info.getServletResponse().addCookie(
                                        createRememberMeCookie(token));
                            }
                        });
    }

    @Override
    public AuthenticationResult tryAuthenticate(
            RememberMeAuthenticationRequest request) {
        // extract the token from the request
        Cookie cookie = getCookie(info.getServletRequest(),
                config.rememberMeCookieName);
        if (cookie != null) {
            RememberMeToken token = parseToken(cookie.getValue());
            RememberMeToken storedToken = dao.loadToken(token.getId());
            if (storedToken != null
                    && Arrays
                            .equals(storedToken.getSeries(), token.getSeries())) {
                log.debug("stored token with matching series found");
                Subject subject = dao.loadSubject(token.getId());
                if (Arrays.equals(storedToken.getToken(), token.getToken())) {
                    // token matches, set new token, return success
                    log.debug("token did match, updating cookie");
                    byte[] tmp = new byte[20];
                    random.nextBytes(tmp);
                    RememberMeToken updatedToken = token.withToken(tmp);
                    dao.updateToken(updatedToken);
                    info.getServletResponse().addCookie(
                            createRememberMeCookie(updatedToken));
                    return AuthenticationResult
                            .success(new RememberMeAuthenticationSuccess(
                                    subject));
                } else {
                    log.warn("token theft detected, subject is " + subject);
                    // series did match, but token did not. There appears to
                    // have been a token theft
                    return AuthenticationResult
                            .failure(new RememberMeTokenTheftFailure(subject));
                }
            }
        } else {
            log.debug("No remember me token found in request");
        }

        // no cookie present, or no token for the series found
        return AuthenticationResult
                .failure(new NoRememberMeTokenFoundAuthenticationFailure());
    }

    private RememberMeToken createToken() {
        byte[] series = new byte[20];
        byte[] token = new byte[20];
        random.nextBytes(series);
        random.nextBytes(token);
        return new RememberMeToken(0, series, token);
    }

    private Cookie createRememberMeCookie(RememberMeToken token) {

        String value = token.getId() + ","
                + Base64.getEncoder().encodeToString(token.getSeries()) + ","
                + Base64.getEncoder().encodeToString(token.getToken());
        Cookie result = new Cookie(config.rememberMeCookieName, value);
        String path = info.getServletContext().getContextPath();
        result.setPath(Strings.isNullOrEmpty(path) ? "/" : path);
        result.setHttpOnly(true);
        return result;
    }

    private Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        return null;
    }

    private RememberMeToken parseToken(String str) {
        String[] parts = str.split(",");
        try {
            long id = Long.parseLong(parts[0]);
            byte[] series = Base64.getDecoder().decode(parts[1]);
            byte[] token = Base64.getDecoder().decode(parts[2]);
            return new RememberMeToken(id, series, token);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while decoding remember me token "
                            + Arrays.toString(parts), e);
        }
    }

    public void setDao(RememberMeTokenDao dao) {
        this.dao = dao;
    }
}
