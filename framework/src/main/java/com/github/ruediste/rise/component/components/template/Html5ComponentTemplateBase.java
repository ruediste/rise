package com.github.ruediste.rise.component.components.template;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.integration.RiseCanvas;

public abstract class Html5ComponentTemplateBase<T extends Component> extends
        ComponentTemplateBase<T> {
    @Inject
    CoreConfiguration config;

    @Override
    final public void doRender(T component, HtmlCanvasTarget target) {
        RiseCanvas<?> html = config.createRiseCanvas(target);
        doRender(component, html);
    }

    abstract public void doRender(T component, RiseCanvas<?> html);
}
