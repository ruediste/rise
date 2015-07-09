package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.RiseCanvas;

public class CSubViewTemplate extends ComponentTemplateBase<CSubView> {

    @Override
    public void doRender(CSubView component, RiseCanvas<?> html) {
        html.renderChildren(component);
    }

}
