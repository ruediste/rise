package com.github.ruediste.rise.testApp.component;

import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste1.i18n.label.Labeled;

@Labeled
public class TestComponentView extends ViewComponent<TestComponentController> {

    @Override
    protected Component createComponents() {
        //@formatter:off
        return new CPage(label(this))

        .add(toComponent(html -> html

                .div().ID("a").add(new CController(controller.subControllerA))._div()

                .div().ID("b").add(new CController(controller.subControllerB))._div()

                .div().ID("main")
                    .span().ID("mainValue").add(toComponentDirect(x->x.write(controller.entity.getValue())))
                    ._span()
                    .add(new CButton("refresh").CLASS("refresh").setHandler(controller::refresh))
                ._div()
            ));
    }
}
