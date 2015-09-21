package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.RiseCanvas;

public class CComponentStackTemplate
        extends ComponentTemplateBase<CComponentStack> {

    @Override
    public void doRender(CComponentStack component, RiseCanvas<?> html) {
        html.render(component.peek());
    }

}
