package com.github.ruediste.rise.sample.welcome;

import com.github.ruediste.rise.component.components.CView;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.core.security.login.LoginControllerBase;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.ViewComponent;
import com.github.ruediste1.i18n.label.Labeled;

public class SampleLoginController extends LoginControllerBase {

    @Labeled
    public static class View extends ViewComponent<SampleLoginController> {

        @Override
        protected void doRender(SampleCanvas html) {
            html.render(new CPage(() -> html.render(new CView(controller.getLoginSubController()))));
        }

    }
}
