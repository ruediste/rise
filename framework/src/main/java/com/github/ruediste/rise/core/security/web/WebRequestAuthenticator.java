package com.github.ruediste.rise.core.security.web;

import java.util.function.BiFunction;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.security.AuthenticationHolder;
import com.github.ruediste.rise.core.security.NoAuthenticationException;
import com.github.ruediste.rise.core.security.RememberMeNotSufficientException;
import com.github.ruediste.rise.core.security.Subject;
import com.github.ruediste.rise.core.security.authentication.AuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.AuthenticationResult;
import com.github.ruediste.rise.core.security.authentication.AuthenticationSuccess;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeAuthenticationRequest;
import com.github.ruediste.rise.core.web.RedirectRenderResult;

/**
 * Takes care of authenticating web requests.
 * <p>
 * First the current session is checked for an already logged in {@link Subject}
 * . If there is no subject the request is checked for remember-me tokens. And
 * finally, the user is redirected to a login form.
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
                    BiFunction<CoreUtil, String, ActionResult> factory = config
                            .getLoginLocationFactory();
                    if (factory == null) {
                        throw new RuntimeException(
                                "CoreConfiguration.loginLocationFactory is not set",
                                e);
                    }

                    log.debug("Redirecting to login location");
                    coreRequestInfo.setActionResult(new RedirectRenderResult(
                            util.toPathInfo(factory.apply(util, coreRequestInfo
                                    .getServletRequest().getPathInfo()))));
                    return;
                }
                t = t.getCause();
            }
            throw e;
        }
    }
}
