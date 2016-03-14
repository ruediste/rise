package com.github.ruediste.rise.component.generic;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.integration.RiseCanvas;

public interface DisplayRenderer<T> {

    void render(RiseCanvas<?> html, T value);

    /**
     * Bind this renderer to a value
     */
    default Renderable<RiseCanvas<?>> renderable(T value) {
        return html -> render(html, value);
    }
}
