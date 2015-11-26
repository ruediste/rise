package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CInputGroupTemplate
        extends BootstrapComponentTemplateBase<CInputGroup> {

    @Override
    public void doRender(CInputGroup component, BootstrapRiseCanvas<?> html) {
        html.bInputGroup().rCOMPONENT_ATTRIBUTES(component)
                .renderChildren(component)._bInputGroup();
    }

}
