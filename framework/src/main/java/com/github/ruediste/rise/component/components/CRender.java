package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.components.template.CRenderHtmlTemplate;
import com.github.ruediste.rise.component.tree.ComponentBase;

@DefaultTemplate(CRenderHtmlTemplate.class)
public class CRender extends ComponentBase<CRender> {

    private final Renderer renderer;

    public CRender(Renderer renderer) {
        this.renderer = renderer;
    }

    public Renderer getRenderer() {
        return renderer;
    }
}
