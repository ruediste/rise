package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.components.template.ComponentTemplateBase;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CComponentContainerTemplate extends
        ComponentTemplateBase<CComponentContainer> {

    @Override
    public void doRender(CComponentContainer component, RiseCanvas<?> html) {
        html.render(children(component));
    }

}
