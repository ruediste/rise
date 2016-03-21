package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CCheckBoxTemplate extends BootstrapComponentTemplateBase<CCheckBox> {

    @Inject
    ComponentUtil util;

    @Override
    public void applyValues(CCheckBox component) {
        getParameterValue(component, "value").ifPresent(value -> component.setChecked("true".equals(value)));
    }

    @Override
    public void doRender(CCheckBox component, BootstrapRiseCanvas<?> html) {
        html.input().TYPE(InputType.checkbox.toString()).BformControl().VALUE(Boolean.toString(component.isChecked()))
                .NAME(util.getKey(component, "value")).rCOMPONENT_ATTRIBUTES(component);
    }
}
