package com.github.ruediste.rise.component;

import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.components.CComponentContainer;
import com.github.ruediste.rise.component.components.CMixedRender;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.integration.RiseCanvasBase;

public class ComponentFactoryUtil {

    @Inject
    CoreConfiguration coreConfiguration;

    @Inject
    BindingUtil bindingUtil;

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

    public Component toComponentBound(Supplier<?> bindingAccessor,
            Renderable<?> renderable) {
        CComponentContainer container = new CComponentContainer();
        BindingUtil.bind(container, bindingAccessor, x -> {
            container.child.setChild(renderToCanvas(renderable));
        }, x -> {
        });
        return container;
    }
}
