package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CTextFieldTemplate extends
        BootstrapComponentTemplateBase<CTextField> {
    @Inject
    ComponentUtil util;

    @Inject
    InputRenderHelper helper;

    @Override
    public void applyValues(CTextField component) {
        getParameterValue(component, "value").ifPresent(component::setText);
    }

    @Override
    public void doRender(CTextField component, BootstrapRiseCanvas<?> html) {
        helper.renderInput(
                component,
                html,
                () -> html.input().TYPE("text").B_FORM_CONTROL()
                        .VALUE(component.getText())
                        .TEST_NAME(component.TEST_NAME())
                        .NAME(util.getKey(component, "value"))
                        .ID(util.getComponentId(component)));
    }
}
