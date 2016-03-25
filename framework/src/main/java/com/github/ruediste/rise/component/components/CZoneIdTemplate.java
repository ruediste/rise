package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CZoneIdTemplate extends BootstrapComponentTemplateBase<CZoneId> {
    @Inject
    ComponentUtil util;

    @Override
    public void doRender(CZoneId component, BootstrapRiseCanvas<?> html) {
        // html.input().TYPE(component.isPassword() ? "password" :
        // "text").BformControl().VALUE(component.getText())
        // .rCOMPONENT_ATTRIBUTES(component).NAME(util.getKey(component,
        // "value"));
    }

    @Override
    public void applyValues(CZoneId component) {
        // getParameterValue(component, "value").ifPresent(component::setText);
    }
}
