package com.github.ruediste.rise.core.security.login;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.FrameworkViewComponent;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.security.authentication.UserNameNotFoundAuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.AuthenticationRequestUsernamePassword;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationResult;
import com.github.ruediste.rise.core.security.web.LoginManager;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.MembersLabeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

/**
 * Subcontroller providing login logic. It's view just displays messages,
 * username and password.
 */
public class LoginSubController extends SubControllerComponent {
    @Inject
    Logger log;

    @Inject
    LabelUtil labelUtil;

    @MembersLabeled
    enum Messages {
        LOGIN_FAILED,

        @Label("Username/Password did not match") USERNAME_PASSWORD_INCORRECT,

        AUTHENTICATION_FAILED,

        @Label("Your Remember Me Token Has been Stolen") TOKEN_TEFT_DETECTED_HEADING,

        @Label("Someone gained access to your remember me cookie. This can be due to someone accessing your computer in person, or by malware installed on your system. "
                + "The token has been invalidated and cannot be used anymore, but in the time since the theft a third person might have accessed your account!") TOKEN_TEFT_DETECTED_BODY
    }

    @Labeled
    public static class LoginView extends FrameworkViewComponent<LoginSubController> {

        @Override
        protected void renderImpl(BootstrapRiseCanvas<?> html) {
            if (controller.data.tokenTheftDetected) {
                html.div().CLASS("panel panel-danger").div().CLASS("panel-heading")
                        .content(Messages.TOKEN_TEFT_DETECTED_HEADING).div().CLASS("panel-body")
                        .content(Messages.TOKEN_TEFT_DETECTED_BODY)._div();
            } else {
                List<LString> msgs = controller.data.messages;
                if (!msgs.isEmpty()) {
                    html.div().CLASS("panel panel-warning").div().CLASS("panel-heading").content(Messages.LOGIN_FAILED)
                            .div().CLASS("panel-body").ul().fForEach(msgs, msg -> html.li().content(msg))._ul()._div()
                            ._div();
                }
            }
            html.add(new CFormGroup(() -> html.input_text().Rvalue(() -> controller.data.userName)));
            html.add(new CFormGroup(() -> html.input_text().Rvalue(() -> controller.data.password)));

            // html.span().write("userName")._span().input_text().VALUE(() ->
            // controller.data.userName);
            // html.span().write("password")._span().input_text().VALUE(() ->
            // controller.data.password);
            html.add(new CButton(controller, c -> c.login()));
        }
    }

    @PropertiesLabeled
    public static class LoginData {
        public String userName = "";
        public String password = "";
        public List<LString> messages = new ArrayList<>();
        public boolean tokenTheftDetected;
    }

    LoginData data = new LoginData();

    @Inject
    CoreRequestInfo requestInfo;

    @Inject
    AuthenticationManager mgr;

    @Inject
    LoginManager loginManager;

    private UrlSpec redirectUrl;

    public LoginSubController setRedirectUrl(UrlSpec redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public LoginSubController setTokenTheftDetected(boolean tokenTheftDetected) {
        data.tokenTheftDetected = tokenTheftDetected;
        return this;

    }

    public LoginSubController setUserPwd(String userName, String password) {
        data.userName = userName;
        data.password = password;
        return this;
    }

    /**
     * Call this from the view to do the login, redirecting to the
     * {@link #redirectUrl} or showing an error message
     */
    @Labeled
    public void login() {
        data.tokenTheftDetected = false;
        AuthenticationRequestUsernamePassword request = new AuthenticationRequestUsernamePassword(data.userName,
                data.password);
        request.setRememberMe(true);

        log.debug("Attempt to log in " + request.getUserName());
        AuthenticationResult result = mgr.authenticate(request);

        if (result.isSuccess()) {
            log.debug("Login sucessful");
            loginManager.login(result.getSuccess());
            closePage(new RedirectRenderResult(redirectUrl));
        } else {
            log.debug("Login failed");
            List<LString> msgs = data.messages;
            msgs.clear();
            for (AuthenticationFailure failure : result.getFailures()) {
                if (failure instanceof UserNameNotFoundAuthenticationFailure)
                    msgs.add(labelUtil.enumMember(Messages.USERNAME_PASSWORD_INCORRECT).label());
                else
                    msgs.add(labelUtil.enumMember(Messages.AUTHENTICATION_FAILED).label());
            }
            data.password = "";
        }
    }
}
