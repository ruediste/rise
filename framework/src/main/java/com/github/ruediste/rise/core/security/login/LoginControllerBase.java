package com.github.ruediste.rise.core.security.login;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste1.i18n.label.Label;

/**
 * Base class for login.
 *
 * <p>
 * To enable login, simply create a subclass with a view rendering the provided
 * {@link LoginSubController}
 */
public abstract class LoginControllerBase extends LoginController {
    @Inject
    Logger log;

    @Inject
    private LoginSubController loginSubController;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.ruediste.rise.core.security.login.LoginController#index(com.
     * github.ruediste.rise.core.web.UrlSpec)
     */
    @Override
    @Label("Login")
    @UrlUnsigned
    public ActionResult index(UrlSpec redirectUrl) {
        loginSubController.setRedirectUrl(redirectUrl);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.ruediste.rise.core.security.login.LoginController#
     * tokenTheftDetected(com.github.ruediste.rise.core.web.UrlSpec)
     */
    @Override
    public ActionResult tokenTheftDetected(UrlSpec redirectUrl) {
        loginSubController.setRedirectUrl(redirectUrl)
                .setTokenTheftDetected(true);
        return null;
    }

    public LoginSubController getLoginSubController() {
        return loginSubController;
    }

}
