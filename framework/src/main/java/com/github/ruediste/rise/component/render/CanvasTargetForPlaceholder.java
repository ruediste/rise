package com.github.ruediste.rise.component.render;

import com.github.ruediste.rendersnakeXT.canvas.DelegatingHtmlCanvasTarget;
import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CanvasTargetForPlaceholder extends DelegatingHtmlCanvasTarget implements RiseCanvasTarget {
    private HtmlCanvasTarget delegate;

    public CanvasTargetForPlaceholder() {
    }

    public CanvasTargetForPlaceholder(HtmlCanvasTarget delegate) {
        this.delegate = delegate;
    }

    @Override
    protected HtmlCanvasTarget getDelegate() {
        return delegate;
    }

    public void setDelegate(HtmlCanvasTarget delegate) {
        this.delegate = delegate;
    }

    @Override
    public void addAttributePlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        throw new RuntimeException(
                "Cannot add placehoders while rendering placeholders. This can only be done in the first render pass");
    }

    @Override
    public void addPlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        throw new RuntimeException(
                "Cannot add placehoders while rendering placeholders. This can only be done in the first render pass");
    }

}
