package com.github.ruediste.rise.sample.welcome;

import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.security.login.LoginControllerBase;
import com.github.ruediste.rise.sample.ViewComponent;

public class SampleLoginController extends LoginControllerBase {

    public static class View extends ViewComponent<SampleLoginController> {

        @Override
        protected Component createComponents() {
            return new CPage().add(new CController(controller.getLoginSubController()));
        }

    }
}
