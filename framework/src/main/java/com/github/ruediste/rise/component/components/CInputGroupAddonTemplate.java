package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CInputGroupAddonTemplate extends BootstrapComponentTemplateBase<CInputGroupAddon> {

    @Override
    public void doRender(CInputGroupAddon component, BootstrapRiseCanvas<?> html) {
        html.bInputGroupAddon().rCOMPONENT_ATTRIBUTES(component).renderChildren(component)._bInputGroupAddon();
    }

}
