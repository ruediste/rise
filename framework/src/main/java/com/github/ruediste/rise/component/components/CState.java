package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.render.ComponentState;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

/**
 * Component used to keep dynamic state in components. All fields declared in
 * subclasses are treated as having an {@link ComponentState} annotation.
 */
public abstract class CState extends Component<CState> {

    static class Template extends BootstrapComponentTemplateBase<CState> {

        @Override
        public void doRender(CState component, BootstrapRiseCanvas<?> html) {
            component.render();
        }
    }

    public abstract void render();

    public CState() {
    }

    public CState(Object key) {
        key(key);
    }
}
