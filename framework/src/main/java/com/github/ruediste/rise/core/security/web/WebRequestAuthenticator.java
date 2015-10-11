package com.github.ruediste.rise.core.security.web;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.security.AuthenticationHolder;
import com.github.ruediste.rise.core.security.NoAuthenticationException;
import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.RememberMeNotSufficientException;
import com.github.ruediste.rise.core.security.authentication.AuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.AuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.AuthenticationResult;
import com.github.ruediste.rise.core.security.authentication.AuthenticationSuccess;
import com.github.ruediste.rise.core.security.authorization.AuthorizationException;
import com.github.ruediste.rise.core.security.login.LoginController;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeCookieAuthenticationRequest;
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
    AuthenticationSessionInfo info;

    @Inject
    CoreUtil util;

    @Override
    public void run(Runnable next) {
        AuthenticationSuccess success = info.getSuccess();

        if (success == null) {
            AuthenticationResult result = authenticationManager
                    .authenticate(new RememberMeCookieAuthenticationRequest());
            if (result.isSuccess()) {
                success = result.getSuccess();
                info.setSuccess(success);
            } else {
                for (AuthenticationFailure failure : result.getFailures()) {
                    if (failure instanceof RememberMeTokenTheftFailure) {
                        coreRequestInfo.setActionResult(
                                new RedirectRenderResult(util.toUrlSpec(util
                                        .go(LoginController.class)
                                        .tokenTheftDetected(
                                                coreRequestInfo.getRequest()
                                                        .createUrlSpec()))));
                        if (coreRequestInfo.getActionResult() != null)
                            return;
                    }
                }
            }
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
            Throwable t = e;
            while (t != null) {
                if (t instanceof NoAuthenticationException
                        || t instanceof RememberMeNotSufficientException
                        || (success == null
                                && t instanceof AuthorizationException)) {
                    coreRequestInfo.setActionResult(new RedirectRenderResult(
                            util.toUrlSpec(util.go(LoginController.class)
                                    .index(coreRequestInfo.getRequest()
                                            .createUrlSpec()))));
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
