package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.RiseCanvas;

public class CSwitchTemplate extends ComponentTemplateBase<CSwitch<?>> {

    @Override
    public void doRender(CSwitch<?> component, RiseCanvas<?> html) {
        component.getCurrentComponent().ifPresent(html::render);
    }
}
