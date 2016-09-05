package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

public class ConClick extends Component<ConClick> {

    private Runnable handler;

    public ConClick() {
    }

    public ConClick(Runnable handler) {
        this.handler = handler;

    }

    public static class Template extends ComponentTemplateBase<ConClick> {

        @Override
        public void processActions(ConClick component) {
            if (util.isParameterDefined(component, "clicked"))
                component.getHandler().run();
        }

        @Override
        public void doRender(ConClick component, RiseCanvas<?> html) {
            html.DATA("rise-on-click", util.getParameterKey(component, "clicked"));
        }

    }

    public Runnable getHandler() {
        return handler;
    }

    public ConClick setHandler(Runnable handler) {
        this.handler = handler;
        return this;
    }

}
