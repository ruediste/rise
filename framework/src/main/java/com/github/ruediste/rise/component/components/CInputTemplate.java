package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CInputTemplate extends BootstrapComponentTemplateBase<CInput> {
    @Inject
    ComponentUtil util;

    @Override
    public void applyValues(CInput component) {
        getParameterValue(component, "value").ifPresent(component::setValue);
    }

    @Override
    public void doRender(CInput component, BootstrapRiseCanvas<?> html) {
        InputType inputType = component.getInputType();
        if (inputType == null)
            throw new RuntimeException("Input type of CInput not set");

        html.input().TYPE(inputType.toString()).B_FORM_CONTROL()
                .VALUE(component.getValue()).TEST_NAME(component.TEST_NAME())
                .NAME(util.getKey(component, "value"))
                .ID(util.getComponentId(component));
    }
}
