package com.github.ruediste.rise.component;

import java.util.function.Function;
import java.util.function.Supplier;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvas;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CDirectRender;
import com.github.ruediste.rise.component.tree.Component;

/**
 * Provide various methods to create components based on a certain type of
 * canvas.
 */
public interface ComponentFactory<TCanvas extends HtmlCanvas<?>> {

    ComponentFactoryUtil internal_componentFactoryUtil();

    /**
     * Create a component containing a rendered form of the given renderable.
     * The renderable will be evaluated only once. Usage:
     * 
     * <pre>
     * {@code
     * toComponent(html -> html
     *   .div.CLASS("foo")
     *     .add(new CButton("hello"))
     *   ._div());
     * }
     * </pre>
     */
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

    /**
     * Create a component rendering the given renderable. The renderable is
     * reevaluated whenever the given binding group is
     * {@link BindingGroup#pullUp() pulled up}.
     */
    default Component toComponentBound(Supplier<?> bindingAccessor,
            Renderable<TCanvas> renderable) {
        return internal_componentFactoryUtil().toComponentBound(
                bindingAccessor, renderable);
    }

    default Component toSubView(ViewComponentBase<?> view) {
        return internal_componentFactoryUtil().toSubView(view);
    }

    default Component toSubView(Object controller) {
        return internal_componentFactoryUtil().toSubView(controller);
    }

    default <T> Component toSubView(Supplier<T> bindingGroupAccessor,
            Function<T, Object> controllerAccessor) {

        return internal_componentFactoryUtil().toSubView(bindingGroupAccessor,
                controllerAccessor);
    }
}
