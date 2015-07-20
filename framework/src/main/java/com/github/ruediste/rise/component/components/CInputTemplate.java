package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CInputTemplate extends BootstrapComponentTemplateBase<CInput> {
    @Inject
    ComponentUtil util;

    @Inject
    InputRenderHelper helper;

    @Override
    public void applyValues(CInput component) {
        component.setValue(util.getParameterValue(component, "value"));
    }

    @Override
    public void doRender(CInput component, BootstrapRiseCanvas<?> html) {
        InputType inputType = component.getInputType();
        if (inputType == null)
            throw new RuntimeException("Input type of CInput not set");

        helper.renderInput(
                component,
                html,
                () -> html.input().TYPE(inputType.toString()).B_FORM_CONTROL()
                        .VALUE(component.getValue())
                        .NAME(util.getKey(component, "value"))
                        .ID(util.getComponentId(component)));
    }
}
