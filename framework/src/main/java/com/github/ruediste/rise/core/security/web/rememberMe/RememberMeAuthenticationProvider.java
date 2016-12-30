package com.github.ruediste.rise.core.security.web.rememberMe;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.RememberMeAwareAuthenticationRequest;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationProvider;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationRequest;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationResult;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;
import com.github.ruediste.rise.core.security.web.LoginManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

/**
 * Perform authentication based on a remember me cookie contained in the web
 * request.
 */
public class RememberMeAuthenticationProvider implements AuthenticationProvider<RememberMeCookieAuthenticationRequest> {

    private static final AttachedProperty<AuthenticationSuccess, Long> tokenIdProperty = new AttachedProperty<>(
            "tokenId");
    @Inject
    Logger log;

    @Inject
    CoreConfiguration config;

    @Inject
    CoreRequestInfo info;

    @Inject
    LoginManager loginManager;

    private SecureRandom random = new SecureRandom();

    private RememberMeTokenDao dao;

    @Override
    public void initialize(AuthenticationManager mgr) {

        mgr.postAuthenticationEvent().addListener(pair -> {
            AuthenticationRequest req = pair.getA();
            AuthenticationResult res = pair.getB();
            if (req instanceof RememberMeAwareAuthenticationRequest
                    && ((RememberMeAwareAuthenticationRequest) req).isRememberMe() && res.isSuccess()) {
                // set the remember me token with a new series
                log.debug("Successful login with remember me set to true, adding cookie to the response");
                RememberMeToken token = createToken();
                token = dao.newToken(token, res.getSuccess().getPrincipal());
                info.getServletResponse().addCookie(createRememberMeCookie(token));
                tokenIdProperty.set(res.getSuccess(), token.getId());
            }
        });

        loginManager.logoutEvent().addListener(success -> {
            Long tokenId = tokenIdProperty.get(success);
            if (tokenId != null) {
                dao.delete(tokenId);
                log.debug("removed token tokenId");
            }
        });

    }

    @Override
    public AuthenticationResult authenticate(RememberMeCookieAuthenticationRequest request) {
        AuthenticationResult failure = AuthenticationResult.failure(new NoRememberMeTokenFoundAuthenticationFailure());

        // extract the token from the request
        Cookie cookie = getCookie(info.getServletRequest(), config.rememberMeCookieName);
        if (cookie == null) {
            log.debug("No remember me token found in request");
            return failure;
        }

        // parse cookie
        RememberMeToken token;
        try {
            token = parseToken(cookie.getValue());
        } catch (Exception e) {
            log.warn("Error while parsing cookie", e);
            return failure;
        }

        // load token
        RememberMeToken storedToken = dao.loadToken(token.getId());
        if (storedToken == null) {
            log.debug("Token not found in database");
            return failure;
        }

        // compare series
        if (!Arrays.equals(storedToken.getSeries(), token.getSeries())) {
            log.warn("Series of remember me token did not match series in token. Is someone attacking?");
            return failure;
        }

        log.debug("stored token with matching series found");
        Principal principal = dao.loadPrincipal(token.getId());
        if (Arrays.equals(storedToken.getToken(), token.getToken())) {
            // token matches, set new token, return success
            log.debug("token did match, updating cookie");
            byte[] tmp = new byte[20];
            random.nextBytes(tmp);
            RememberMeToken updatedToken = token.withToken(tmp);
            dao.updateToken(updatedToken);
            info.getServletResponse().addCookie(createRememberMeCookie(updatedToken));
            RemberMeAuthenticationSuccess success = new RemberMeAuthenticationSuccess(principal);
            tokenIdProperty.set(success, token.getId());
            return AuthenticationResult.success(success);
        } else {
            // series did match, but token did not. There appears to
            // have been a token theft
            log.warn("token theft detected, principal is " + principal);
            dao.delete(token.getId());
            return AuthenticationResult.failure(new RememberMeTokenTheftFailure(principal));
        }

    }

    private RememberMeToken createToken() {
        byte[] series = new byte[20];
        byte[] token = new byte[20];
        random.nextBytes(series);
        random.nextBytes(token);
        return new RememberMeToken(0, series, token);
    }

    private Cookie createRememberMeCookie(RememberMeToken token) {

        String value = token.getId() + "~" + Base64.getEncoder().encodeToString(token.getSeries()) + "~"
                + Base64.getEncoder().encodeToString(token.getToken());
        Cookie result = new Cookie(config.rememberMeCookieName, value);
        String path = info.getServletContext().getContextPath();
        result.setPath(Strings.isNullOrEmpty(path) ? "/" : path);
        result.setHttpOnly(true);
        result.setMaxAge((int) TimeUnit.DAYS.toSeconds(30));
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

    @VisibleForTesting
    public static RememberMeToken parseToken(String str) {
        String[] parts = str.split("~");
        try {
            long id = Long.parseLong(parts[0]);
            byte[] series = Base64.getDecoder().decode(parts[1]);
            byte[] token = Base64.getDecoder().decode(parts[2]);
            return new RememberMeToken(id, series, token);
        } catch (Exception e) {
            throw new RuntimeException("Error while decoding remember me token " + Arrays.toString(parts), e);
        }
    }

    public void setDao(RememberMeTokenDao dao) {
        this.dao = dao;
    }
}
