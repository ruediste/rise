package com.github.ruediste.rise.testApp.component;

import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.testApp.TestCanvas;
import com.github.ruediste1.i18n.label.Labeled;

@Labeled
public class TestComponentView extends ViewComponent<TestComponentController> {

    @Override
    protected void render(TestCanvas html) {
        html.add(new CPage(() -> {
            html.div().ID("a").addController(controller.subControllerA)._div();
            html.div().ID("b").addController(controller.subControllerB)._div();
            html.div().ID("main").span().ID("mainValue").write(() -> controller.entity.getValue())._span()
                    .add(new CButton("refresh").CLASS("refresh").setHandler(controller::refresh))._div();
        }));
    }
}
