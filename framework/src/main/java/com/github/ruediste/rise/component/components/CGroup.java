package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

/**
 * Component without any own markup, used to group components
 */
public class CGroup extends Component<CGroup> {

    private Runnable body;

    static class Template extends BootstrapComponentTemplateBase<CGroup> {

        @Override
        public void doRender(CGroup component, BootstrapRiseCanvas<?> html) {
            component.getBody().run();
        }

    }

    public CGroup() {

    }

    public CGroup(Runnable body) {
        this.body = body;
    }

    public Runnable getBody() {
        return body;
    }

    public CGroup body(Runnable body) {
        this.body = body;
        return this;
    }

}
