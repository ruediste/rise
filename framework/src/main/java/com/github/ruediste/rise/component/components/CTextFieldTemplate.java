package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CTextFieldTemplate
        extends BootstrapComponentTemplateBase<CTextField> {
    @Inject
    ComponentUtil util;

    @Override
    public void applyValues(CTextField component) {
        getParameterValue(component, "value").ifPresent(component::setText);
    }

    @Override
    public void doRender(CTextField component, BootstrapRiseCanvas<?> html) {
        html.input().TYPE("text").BformControl().VALUE(component.getText())
                .TEST_NAME(component.TEST_NAME())
                .NAME(util.getKey(component, "value"))
                .ID(util.getComponentId(component));
    }
}
