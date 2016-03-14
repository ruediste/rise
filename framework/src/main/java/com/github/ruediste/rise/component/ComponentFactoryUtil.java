package com.github.ruediste.rise.component;

import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.components.CComponentContainer;
import com.github.ruediste.rise.component.components.CMixedRender;
import com.github.ruediste.rise.component.components.CSubView;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.integration.RiseCanvasBase;

public class ComponentFactoryUtil {

    @Inject
    CoreConfiguration coreConfiguration;

    @Inject
    ComponentViewRepository repository;

    /**
     * @see ComponentFactory#toComponent(Renderable)
     */
    public Component toComponent(Renderable<?> renderable) {
        return renderToCanvas(renderable);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected CMixedRender renderToCanvas(Renderable<?> renderable) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
        RiseCanvasBase<?> html = coreConfiguration.createApplicationCanvas();
        html.initializeForComponent(stream);
        ((Renderable) renderable).renderOn(html);
        html.flush();
        return html.internal_riseHelper().getcRender();
    }

    /**
     * @see ComponentFactory#toComponentBound(Supplier, Renderable)
     */
    public Component toComponentBound(Supplier<?> bindingAccessor, Renderable<?> renderable) {
        CComponentContainer container = new CComponentContainer();
        BindingUtil.bind(container, bindingAccessor, x -> {
            container.child.setChild(renderToCanvas(renderable));
        }, x -> {
        });
        return container;
    }

    /**
     * @see ComponentFactory#toSubView(ViewComponentBase)
     */
    public Component toSubView(ViewComponentBase<?> view) {
        return new CSubView(view);
    }

    /**
     * @see ComponentFactory#toSubView(Object)
     */
    public Component toSubView(Object controller) {
        return toSubView(repository.createView(controller));
    }

    /**
     * @see ComponentFactory#toSubView(Supplier, Function)
     */
    public <T> Component toSubView(Supplier<T> bindingGroupAccessor, Function<T, Object> controllerAccessor) {
        CSubView result = new CSubView();

        BindingUtil.bind(result, bindingGroupAccessor, new Consumer<T>() {
            private Object controller;

            @Override
            public void accept(T x) {
                Object newController = controllerAccessor.apply(x);
                if (newController != controller) {
                    controller = newController;
                    result.setView(repository.createView(controller));
                }
            }
        }, x -> {
        });

        return result;
    }
}
