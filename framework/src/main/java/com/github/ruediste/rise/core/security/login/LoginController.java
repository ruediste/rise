package com.github.ruediste.rise.core.security.login;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.web.WebRequestAuthenticator;
import com.github.ruediste.rise.core.web.UrlSpec;

/**
 * Interface used by the {@link WebRequestAuthenticator} to show a login page.
 */
public abstract class LoginController extends ControllerComponent {

    public abstract ActionResult index(UrlSpec redirectUrl);

    public abstract ActionResult tokenTheftDetected(UrlSpec redirectUrl);

}