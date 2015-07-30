package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.RiseCanvas;

public class CGroupTemplate extends ComponentTemplateBase<CGroup> {

    @Override
    public void doRender(CGroup component, RiseCanvas<?> html) {
        html.renderChildren(component);
    }

}
