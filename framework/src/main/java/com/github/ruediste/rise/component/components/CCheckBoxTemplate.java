package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CCheckBoxTemplate extends BootstrapComponentTemplateBase<CCheckBox> {

    @Inject
    ComponentUtil util;

    @Override
    public void applyValues(CCheckBox component) {
        getParameterObject(component, "isChecked").ifPresent(value -> component.setChecked((boolean) value));
    }

    @Override
    public void doRender(CCheckBox component, BootstrapRiseCanvas<?> html) {
        html.input().TYPE(InputType.checkbox.toString()).BformControl().CLASS("rise_c_checkbox")
                .fIf(component.isChecked(), () -> html.CHECKED("checked")).VALUE("true")
                .NAME(util.getKey(component, "value")).rCOMPONENT_ATTRIBUTES(component);
    }
}
