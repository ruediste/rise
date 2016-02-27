package com.github.ruediste.rise.core.security.login;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.FrameworkViewComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.component.components.CGroup;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.security.authentication.UserNameNotFoundAuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.UsernamePasswordAuthenticationRequest;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationResult;
import com.github.ruediste.rise.core.security.web.LoginManager;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.rise.core.web.UrlSpec;
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
    public static class LoginView
            extends FrameworkViewComponent<LoginSubController> {

        @Override
        protected Component createComponents() {
            return new CGroup()
                    .add(toComponentBound(() -> controller.data(), html -> {
                        //@formatter:off
                            if (controller.data().isTokenTheftDetected()) {
                                html.div().CLASS("panel panel-danger")
                                    .div().CLASS("panel-heading").content(Messages.TOKEN_TEFT_DETECTED_HEADING)
                                    .div().CLASS("panel-body").content(Messages.TOKEN_TEFT_DETECTED_BODY)
                                ._div();
                            }
                            else{
                                List<LString> msgs=controller.data().getMessages();
                                        if (!msgs.isEmpty()) {
                                    html.div().CLASS("panel panel-warning")
                                      .div().CLASS("panel-heading").content(Messages.LOGIN_FAILED)
                                      .div().CLASS("panel-body")
                                          .ul()
                                          .fForEach(msgs, msg->html.li().content(msg))
                                          ._ul()
                                      ._div()
                                    ._div();
                                    }
                                }
                            //@formatter:on
                    }))
                    .add(new CFormGroup(new CTextField()
                            .bindText(() -> controller.data().getUserName())))
                    .add(new CFormGroup(new CTextField().toPassword()
                            .bindText(() -> controller.data().getPassword())))
                    .add(new CButton(controller, c -> c.login()));
        }
    }

    @PropertiesLabeled
    public static class LoginData {
        private String userName = "";
        private String password = "";
        private List<LString> messages = new ArrayList<>();
        private boolean tokenTheftDetected;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public List<LString> getMessages() {
            return messages;
        }

        public void setMessage(List<LString> message) {
            this.messages = message;
        }

        public boolean isTokenTheftDetected() {
            return tokenTheftDetected;
        }

        public void setTokenTheftDetected(boolean tokenTheftDetected) {
            this.tokenTheftDetected = tokenTheftDetected;
        }
    }

    @Inject
    BindingGroup<LoginData> data;

    public LoginData data() {
        return data.proxy();
    }

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

    public LoginSubController setTokenTheftDetected(
            boolean tokenTheftDetected) {
        data.get().setTokenTheftDetected(tokenTheftDetected);
        return this;

    }

    public LoginSubController setUserPwd(String userName, String password) {
        data.get().setUserName(userName);
        data.get().setPassword(password);
        return this;
    }

    /**
     * Call this from the view to do the login, redirecting to the
     * {@link #redirectUrl} or showing an error message
     */
    @Labeled
    public void login() {
        data.pushDown();
        data.get().setTokenTheftDetected(false);
        UsernamePasswordAuthenticationRequest request = new UsernamePasswordAuthenticationRequest(
                data.get().getUserName(), data.get().getPassword());
        request.setRememberMe(true);

        log.debug("Attempt to log in " + request.getUserName());
        AuthenticationResult result = mgr.authenticate(request);

        if (result.isSuccess()) {
            log.debug("Login sucessful");
            loginManager.login(result.getSuccess());
            closePage(new RedirectRenderResult(redirectUrl));
        } else {
            log.debug("Login failed");
            List<LString> msgs = data.get().getMessages();
            msgs.clear();
            for (AuthenticationFailure failure : result.getFailures()) {
                if (failure instanceof UserNameNotFoundAuthenticationFailure)
                    msgs.add(labelUtil
                            .enumMember(Messages.USERNAME_PASSWORD_INCORRECT)
                            .label());
                else
                    msgs.add(
                            labelUtil.enumMember(Messages.AUTHENTICATION_FAILED)
                                    .label());
            }
            data.get().setPassword("");
            data.pullUp();
        }
    }
}
