package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

public class RenderChildrenTemplate extends ComponentTemplateBase<Component> {

    @Override
    public void doRender(Component component, RiseCanvas<?> html) {
        html.renderChildren(component);
    }

}
