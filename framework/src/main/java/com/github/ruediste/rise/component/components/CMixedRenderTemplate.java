package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.RiseCanvas;

public class CMixedRenderTemplate extends Html5ComponentTemplateBase<CMixedRender> {

    @Override
    public void doRender(CMixedRender component, RiseCanvas<?> html) {
        component.doRender(html, util);
    }

}
