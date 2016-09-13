package com.github.ruediste.rise.testApp.component;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CDisplay;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.ConClickToggle;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.testApp.TestCanvas;
import com.github.ruediste1.i18n.label.Labeled;

public class ClientSideTreeController extends ControllerComponent {

    static class View extends ViewComponent<ClientSideTreeController> {

        @Override
        protected void renderImpl(TestCanvas html) {
            CDisplay displayed = new CDisplay(true);
            html.add(new CPage(() -> {
                html.ul().li().span().CLASS("head").add(new ConClickToggle(displayed)).content("Hello").ul()
                        .add(displayed).li().span().CLASS("head").content("World")._li()._ul()._li()._ul();
                html.add(new CButton(controller, x -> x.foo()));
            }));
        }
    }

    @Labeled
    void foo() {
    }

    @UrlUnsigned
    public ActionResult index() {
        return null;
    }
}
