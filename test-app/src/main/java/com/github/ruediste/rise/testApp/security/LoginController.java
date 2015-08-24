package com.github.ruediste.rise.testApp.security;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.security.authentication.AuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.AuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.AuthenticationResult;
import com.github.ruediste.rise.core.security.authentication.UserNameNotFoundAuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.UsernamePasswordAuthenticationRequest;
import com.github.ruediste.rise.core.security.web.AuthenticationSessionInfo;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.rise.testApp.component.ViewComponent;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.MembersLabeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;
import com.google.common.base.Strings;

public class LoginController extends ControllerComponent {
    @Inject
    Logger log;

    @Inject
    LabelUtil labelUtil;

    @MembersLabeled
    enum Messages {
        LOGIN_FAILED,

        @Label("Username/Password did not match")
        USERNAME_PASSWORD_INCORRECT,

        AUTHENTICATION_FAILED,

        @Label("Your Remember Me Token Has been Stolen")
        TOKEN_TEFT_DETECTED_HEADING,

        @Label("Someone gained access to your remember me cookie. This can be due to someone accessing your computer in person, or by malware installed on your system. "
                + "The token has been deleted and cannot be used anymore, but in the time since the theft a third person might have accessed your account!")
        TOKEN_TEFT_DETECTED_BODY
    }

    @Labeled
    public static class LoginView extends ViewComponent<LoginController> {

        @Override
        protected Component createComponents() {
            return new CPage()
                    .add(toComponentBound(
                            () -> controller.data(),html ->{
                                //@formatter:off
                            if (controller.data().isTokenTheftDetected()) {
                                html.div().CLASS("panel panel-error")
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
                    .add(new CFormGroup(new CTextField()
                            .bindText(() -> controller.data().getPassword())))
                    .add(new CButton(controller, c -> c.login()));
        }
    }

    @PropertiesLabeled
    static class LoginData {
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

    BindingGroup<LoginData> data = new BindingGroup<>(new LoginData());

    LoginData data() {
        return data.proxy();
    }

    @Inject
    CoreRequestInfo requestInfo;

    @Inject
    AuthenticationManager mgr;

    @Inject
    AuthenticationSessionInfo info;

    private String originalPathInfo;

    @Label("Login")
    public ActionResult index() {
        setOriginalPathInfoToReferer();
        return null;
    }

    @Label("Login")
    public ActionResult index(String originalPathInfo) {
        this.originalPathInfo = originalPathInfo;
        return null;
    }

    public ActionResult tokenTheftDetected(String originalPathInfo) {
        this.originalPathInfo = originalPathInfo;
        data.get().setTokenTheftDetected(true);
        return null;
    }

    private void setOriginalPathInfoToReferer() {
        String referer = requestInfo.getServletRequest().getHeader("Referer");
        if (Strings.isNullOrEmpty(referer))
            throw new RuntimeException("Referer is not set");
        originalPathInfo = referer;
    }

    @Labeled
    void login() {
        data.pushDown();
        data.get().setTokenTheftDetected(false);
        UsernamePasswordAuthenticationRequest request = new UsernamePasswordAuthenticationRequest(
                data.get().getUserName(), data.get().getPassword());
        request.setRememberMe(true);

        log.debug("Attempt to log in " + request.getUserName());
        AuthenticationResult result = mgr.authenticate(request);

        if (result.isSuccess()) {
            log.debug("Login sucessful");
            info.setSuccess(result.getSuccess());
            closePage(new RedirectRenderResult(new PathInfo(originalPathInfo)));
        } else {
            log.debug("Login failed");
            List<LString> msgs = data.get().getMessages();
            msgs.clear();
            for (AuthenticationFailure failure : result.getFailures()) {
                if (failure instanceof UserNameNotFoundAuthenticationFailure)
                    msgs.add(labelUtil
                            .getEnumMemberLabel(Messages.USERNAME_PASSWORD_INCORRECT));
                else
                    msgs.add(labelUtil
                            .getEnumMemberLabel(Messages.AUTHENTICATION_FAILED));
            }
            data.pullUp();
        }
    }
}
