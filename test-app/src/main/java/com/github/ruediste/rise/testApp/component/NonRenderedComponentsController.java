package com.github.ruediste.rise.testApp.component;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CCheckBox;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CSwitch;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.MembersLabeled;

public class NonRenderedComponentsController extends ControllerComponent {

    @Labeled
    public static class View extends ViewComponent<NonRenderedComponentsController> {

        @MembersLabeled
        enum Choice {
            A, B
        }

        @Override
        protected Component createComponents() {
            CCheckBox checkBox = new CCheckBox();
            checkBox.setChecked(true);
            AtomicInteger count = new AtomicInteger(0);
            return new CPage()
                    .render(new CSwitch<Choice>().put(Choice.A, new CText("a")).put(Choice.B, checkBox)
                            .setOption(Choice.A))
                    .render(new CButton(controller, x -> x.reload())).render(toComponentDirect(html -> html.span()
                            .TEST_NAME("checked").content(count.incrementAndGet() + "" + checkBox.isChecked())));
        }

    }

    @Labeled
    public void reload() {
    }

    @UrlUnsigned
    public ActionResult index() {
        return null;
    }
}
