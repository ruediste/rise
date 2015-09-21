package com.github.ruediste.rise.core.security.web;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.security.AuthenticationHolder;
import com.github.ruediste.rise.core.security.NoAuthenticationException;
import com.github.ruediste.rise.core.security.RememberMeNotSufficientException;
import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.AuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.AuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.AuthenticationResult;
import com.github.ruediste.rise.core.security.authentication.AuthenticationSuccess;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeAuthenticationRequest;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeTokenTheftFailure;

/**
 * Takes care of authenticating web requests.
 * <p>
 * First the current session is checked for an already logged in
 * {@link Principal} . If there is no subject the request is checked for
 * remember-me tokens. And finally, the user is redirected to a login form.
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
    CoreConfiguration config;

    @Inject
    CoreUtil util;

    @Override
    public void run(Runnable next) {
        AuthenticationSuccess success = info.getSuccess();

        if (success == null) {
            AuthenticationResult result = authenticationManager
                    .authenticate(new RememberMeAuthenticationRequest());
            if (result.isSuccess()) {
                success = result.getSuccess();
                info.setSuccess(success);
            } else {
                for (AuthenticationFailure failure : result.getFailures()) {
                    if (failure instanceof RememberMeTokenTheftFailure) {
                        Runnable handler = config
                                .getRememberMeTokenTheftHandler();
                        if (handler == null)
                            log.error(
                                    "Thoken theft detected, but no tokenThenftHandler was defined in CoreConfiguration");
                        handler.run();
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
                        || t instanceof RememberMeNotSufficientException) {
                    Runnable factory = config.loginHandler();
                    if (factory == null) {
                        throw new RuntimeException(
                                "CoreConfiguration.loginHandler is not set", e);
                    }

                    log.debug("running login handler");
                    factory.run();
                    return;
                }
                t = t.getCause();
            }
            throw e;
        }
    }
}
