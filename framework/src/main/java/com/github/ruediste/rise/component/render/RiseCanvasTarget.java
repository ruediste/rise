package com.github.ruediste.rise.component.render;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.integration.RiseCanvas;

public interface RiseCanvasTarget extends HtmlCanvasTarget {

    default void addAttributePlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        throw new RuntimeException("Cannot add placehoders while not in the first rendering pass of a component view");
    }

    default void addPlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        throw new RuntimeException("Cannot add placehoders while not in the first rendering pass of a component view");

    }

}
