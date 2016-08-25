package com.github.ruediste.rise.component.render;

import com.github.ruediste.rendersnakeXT.canvas.DelegatingHtmlCanvasTarget;
import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;

public class CanvasTargetMvc extends DelegatingHtmlCanvasTarget {

    private HtmlCanvasTarget delegate;

    @Override
    protected HtmlCanvasTarget getDelegate() {
        return delegate;
    }

    public void setDelegate(HtmlCanvasTarget delegate) {
        this.delegate = delegate;
    }

}
