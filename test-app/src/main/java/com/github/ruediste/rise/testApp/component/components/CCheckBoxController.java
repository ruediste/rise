package com.github.ruediste.rise.testApp.component.components;

import java.util.Objects;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.components.CCheckBox;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.testApp.component.ViewComponent;

public class CCheckBoxController extends ControllerComponent {

    public static class View extends ViewComponent<CCheckBoxController> {

        @Override
        protected Component createComponents() {
            CText text = new CText().TEST_NAME("text");
            return new CPage().render(new CCheckBox().TEST_NAME("checkBox")
                    .setToggledHandler(x -> text.setTextString(Objects.toString(x)))).render(text);
        }
    }

    @UrlUnsigned
    public ActionResult index() {
        return null;
    }
}
