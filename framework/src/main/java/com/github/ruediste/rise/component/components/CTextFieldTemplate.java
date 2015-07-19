package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CTextFieldTemplate extends
        BootstrapComponentTemplateBase<CTextField> {
    @Inject
    ComponentUtil util;

    @Inject
    FormGroupTemplateHelper helper;

    @Override
    public void applyValues(CTextField component) {
        component.setText(util.getParameterValue(component, "value"));
    }

    @Override
    public void doRender(CTextField component,
            BootstrapRiseCanvas<?> html) {
        helper.renderFormGroup(
                component,
                html,
                () -> html.input().TYPE("text").B_FORM_CONTROL()
                        .VALUE(component.getText())
                        .NAME(util.getKey(component, "value"))
                        .ID(util.getComponentId(component)));
    }
}
