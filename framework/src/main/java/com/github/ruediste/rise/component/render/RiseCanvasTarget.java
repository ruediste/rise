package com.github.ruediste.rise.component.render;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

public interface RiseCanvasTarget extends HtmlCanvasTarget {

    default void addAttributePlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        throw new RuntimeException("Placeholders can only be added in the first rendering pass of a component view");
    }

    default void addPlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        throw new RuntimeException("Placeholders can only be added in the first rendering pass of a component view");

    }

    default void add(RiseCanvas<?> html, Component<?> component) {
        throw new RuntimeException("Components can only be added in the first rendering pass of a component view");
    }

    default ViewComponentBase<?> getView() {
        throw new UnsupportedOperationException();
    }

    default void setView(ViewComponentBase<?> view) {
        throw new UnsupportedOperationException();
    }

    default Component<?> getParent() {
        throw new UnsupportedOperationException();
    }

    default void suspendOutput(boolean suspend) {
        throw new UnsupportedOperationException();
    }

}
