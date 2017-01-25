package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

public class ConEnter extends Component<ConEnter> {

    private Runnable handler;

    public ConEnter() {
    }

    public ConEnter(Runnable handler) {
        this.handler = handler;

    }

    public static class Template extends ComponentTemplateBase<ConEnter> {

        @Override
        public void processActions(ConEnter component) {
            if (util.isParameterDefined(component, "pressed"))
                component.getHandler().run();
        }

        @Override
        public void doRender(ConEnter component, RiseCanvas<?> html) {
            html.DATA("rise-on-enter-key", util.getParameterKey(component, "pressed"));
        }

    }

    public Runnable getHandler() {
        return handler;
    }

    public ConEnter setHandler(Runnable handler) {
        this.handler = handler;
        return this;
    }

}
