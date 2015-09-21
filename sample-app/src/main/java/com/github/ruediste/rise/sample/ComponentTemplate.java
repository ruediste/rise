package com.github.ruediste.rise.sample;

import com.github.ruediste.rise.component.components.ComponentTemplateBase;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

public abstract class ComponentTemplate<T extends Component>
        extends ComponentTemplateBase<T> {

    @Override
    public void doRender(T component, RiseCanvas<?> html) {
        doRender(component, (SampleCanvas) html);
    }

    public abstract void doRender(T component, SampleCanvas html);
}
