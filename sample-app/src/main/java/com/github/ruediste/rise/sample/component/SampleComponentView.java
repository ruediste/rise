package com.github.ruediste.rise.sample.component;

import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CTextFieldFormGroup;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.sample.ViewComponent;

public class SampleComponentView extends
        ViewComponent<SampleComponentController> {

    @Override
    protected Component createComponents() {
        return new CPage()
                .add(toComponent(html -> html
                        .write("Wird schon gut sein... " + controller.counter)
                        .add(new CButton("ClickMe").handler(() -> controller
                                .inc()))
                        .add(new CTextFieldFormGroup().setLabel("Text").bind(
                                field -> field.setText(controller.getData()
                                        .getText())))));
    }
}
