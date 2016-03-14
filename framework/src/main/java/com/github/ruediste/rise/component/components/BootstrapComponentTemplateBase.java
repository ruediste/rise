package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvas;

public abstract class BootstrapComponentTemplateBase<T extends Component> extends ComponentTemplateBase<T> {
    @Inject
    CoreConfiguration config;

    @Override
    final public void doRender(T component, RiseCanvas<?> html) {
        doRender(component, (BootstrapRiseCanvas<?>) html);
    }

    abstract public void doRender(T component, BootstrapRiseCanvas<?> html);
}
