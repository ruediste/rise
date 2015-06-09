package com.github.ruediste.rise.component.components;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.tree.ComponentBase;

/**
 * A component directly rendering a {@link Renderable} to the output
 */
@DefaultTemplate(CDirectRenderTemplate.class)
public class CDirectRender extends ComponentBase<CDirectRender> {

    private Renderable<?> renderable;

    public CDirectRender() {

    }

    public CDirectRender(Renderable<?> renderable) {
        this.setRenderable(renderable);

    }

    public Renderable<?> getRenderable() {
        return renderable;
    }

    public CDirectRender setRenderable(Renderable<?> renderable) {
        this.renderable = renderable;
        return this;
    }
}
