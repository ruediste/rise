package com.github.ruediste.rise.component.components;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.components.template.ComponentTemplateBase;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CDirectRenderTemplate extends ComponentTemplateBase<CDirectRender> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void doRender(CDirectRender component, RiseCanvas<?> html) {
        ((Renderable) component.getRenderable()).renderOn(html);
    }
}
