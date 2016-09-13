package com.github.ruediste.rise.component.components;

import java.util.Objects;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class ConClickToggle extends Component<ConClickToggle> {

    Component<?> toggleTarget;

    static class Template extends BootstrapComponentTemplateBase<ConClickToggle> {

        @Override
        public void doRender(ConClickToggle component, BootstrapRiseCanvas<?> html) {
            html.DATA("rise-conclicktoggle-target", Objects.toString(util.getComponentNr(component.toggleTarget)));
        }

    }

    public ConClickToggle() {
    }

    public ConClickToggle(Component<?> toggleTarget) {
        target(toggleTarget);
    }

    public ConClickToggle target(Component<?> toggleTarget) {
        this.toggleTarget = toggleTarget;
        return this;
    }
}
