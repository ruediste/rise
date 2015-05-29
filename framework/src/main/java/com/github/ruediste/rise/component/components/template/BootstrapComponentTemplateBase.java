package com.github.ruediste.rise.component.components.template;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public abstract class BootstrapComponentTemplateBase<T extends Component>
        extends ComponentTemplateBase<T> {
    @Inject
    CoreConfiguration config;

    @Override
    final public void doRender(T component, HtmlCanvasTarget target) {
        BootstrapRiseCanvas<?> html = config.createBootstrapCanvas(target);
        doRender(component, html);
    }

    abstract public void doRender(T component, BootstrapRiseCanvas<?> html);
}
