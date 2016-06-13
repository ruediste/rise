package com.github.ruediste.rise.testApp.security;

import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.core.security.login.LoginControllerBase;
import com.github.ruediste.rise.testApp.TestCanvas;
import com.github.ruediste.rise.testApp.component.ViewComponent;

public class LoginController extends LoginControllerBase {

    public static class View extends ViewComponent<LoginController> {

        @Override
        protected void renderImpl(TestCanvas html) {
            html.render(new CPage(() -> html.render(new CController(controller.getLoginSubController()))));

        }

    }
}
