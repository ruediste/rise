package com.github.ruediste.rise.testApp.security;

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
import com.github.ruediste.rise.core.security.authentication.AuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.AuthenticationResult;
import com.github.ruediste.rise.core.security.authentication.UsernamePasswordAuthenticationRequest;
import com.github.ruediste.rise.core.security.web.AuthenticationSessionInfo;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.rise.testApp.component.ViewComponent;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

public class LoginController extends ControllerComponent {
    @Inject
    Logger log;

    @Labeled
    public static class LoginView extends ViewComponent<LoginController> {

        @Override
        protected Component createComponents() {
            return new CPage()
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
    public ActionResult index(String originalPathInfo) {
        this.originalPathInfo = originalPathInfo;
        return null;
    }

    @Labeled
    void login() {
        data.pushDown();
        UsernamePasswordAuthenticationRequest request = new UsernamePasswordAuthenticationRequest(
                data.get().getUserName(), data.get().getPassword());
        request.setRememberMe(true);

        log.debug("Attempt to log in " + request.getUserName());
        AuthenticationResult result = mgr.authenticate(request);

        if (result.isSuccess()) {
            log.debug("Login sucessful");
            info.setSuccess(result.getSuccess());
            closePage(new RedirectRenderResult(new PathInfo(originalPathInfo)));
        } else
            log.debug("Login failed");
    }
}
