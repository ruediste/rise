package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.components.template.Html5ComponentTemplateBase;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CRenderTemplate extends Html5ComponentTemplateBase<CRender> {

    @Override
    public void doRender(CRender component, RiseCanvas<?> html) {
        component.doRender(html, util);
    }

}
