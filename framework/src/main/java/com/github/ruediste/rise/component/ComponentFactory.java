package com.github.ruediste.rise.component;

import java.util.function.Supplier;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvas;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.components.CDirectRender;
import com.github.ruediste.rise.component.tree.Component;

/**
 * Provide various methods to create components based on a certain type of
 * canvas.
 */
public interface ComponentFactory<TCanvas extends HtmlCanvas<TCanvas>> {

    ComponentFactoryUtil internal_componentFactoryUtil();

    default Component toComponent(Renderable<TCanvas> renderable) {
        return internal_componentFactoryUtil().toComponent(renderable);
    }

    /**
     * Wrap the given canvas in a component rendering directly to the generated
     * output, each time the page is rendered.
     */
    default Component toComponentDirect(Renderable<TCanvas> renderable) {
        return new CDirectRender(renderable);
    }

    default Component toComponentBound(Supplier<?> bindingAccessor,
            Renderable<TCanvas> renderable) {
        return internal_componentFactoryUtil().toComponentBound(
                bindingAccessor, renderable);
    }
}
