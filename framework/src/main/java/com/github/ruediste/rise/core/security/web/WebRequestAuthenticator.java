package com.github.ruediste.rise.core.security.web;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.security.AuthenticationHolder;
import com.github.ruediste.rise.core.security.NoAuthenticationException;
import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationResult;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;
import com.github.ruediste.rise.core.security.authorization.AuthorizationException;
import com.github.ruediste.rise.core.security.authorization.RememberMeNotSufficientException;
import com.github.ruediste.rise.core.security.login.LoginController;
import com.github.ruediste.rise.core.security.web.rememberMe.AuthenticationRequestRememberMeCookie;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeTokenTheftFailure;
import com.github.ruediste.rise.core.web.RedirectRenderResult;

/**
 * Takes care of authenticating web requests.
 * <p>
 * First the current session is checked for an already logged in
 * {@link Principal} . If there is no subject the request is checked for
 * remember-me tokens. If there is still no principal found, the user is
 * redirected to a login form.
 * 
 * <p>
 * 
 */
public class WebRequestAuthenticator extends ChainedRequestHandler {

    @Inject
    Logger log;

    @Inject
    AuthenticationManager authenticationManager;

    @Inject
    AuthenticationHolder subjectManager;

    @Inject
    CoreRequestInfo coreRequestInfo;

    @Inject
    LoginManager loginManager;

    @Inject
    LoginHolder loginHolder;

    @Inject
    CoreUtil util;

    @Override
    public void run(Runnable next) {
        AuthenticationSuccess success = loginHolder.getSuccess();

        if (success == null) {
            try {
                AuthenticationResult result = authenticationManager
                        .authenticate(new AuthenticationRequestRememberMeCookie());
                if (result.isSuccess()) {
                    success = result.getSuccess();
                    log.debug("remember me login successful: {}", success);
                    loginManager.login(success);
                } else {
                    for (AuthenticationFailure failure : result.getFailures()) {
                        if (failure instanceof RememberMeTokenTheftFailure) {
                            coreRequestInfo.setActionResult(
                                    new RedirectRenderResult(util.toUrlSpec(util.go(LoginController.class)
                                            .tokenTheftDetected(coreRequestInfo.getRequest().createUrlSpec()))));
                            if (coreRequestInfo.getActionResult() != null)
                                return;
                        }
                    }
                }
            } catch (Throwable t) {
                log.error("Error while checking remember me", t);
            }
        } else {
            log.debug("found success in login holder: {}", success);
        }

        try {
            if (success != null) {
                log.debug("Using authentication {}", success);
                subjectManager.withAuthentication(success, next);
            } else {
                log.debug("No authentication present");
                next.run();
            }
        } catch (Exception e) {
            // determine if the exception occurred due to insufficient rights.
            // If so, redirec to to the LoginController
            Throwable t = e;
            while (t != null) {
                if (t instanceof NoAuthenticationException || t instanceof RememberMeNotSufficientException
                        || (success == null && t instanceof AuthorizationException)) {
                    coreRequestInfo.setActionResult(new RedirectRenderResult(util.toUrlSpec(
                            util.go(LoginController.class).index(coreRequestInfo.getRequest().createUrlSpec()))));
                    return;
                }
                Throwable cause = t.getCause();
                if (t == cause)
                    break;
                t = cause;
            }
            throw e;
        }
    }
}
