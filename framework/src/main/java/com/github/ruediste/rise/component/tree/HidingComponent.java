package com.github.ruediste.rise.component.tree;

import com.github.ruediste.rise.component.components.ComponentTemplateBase;
import com.github.ruediste.rise.integration.RiseCanvas;

/**
 * Component optionally hiding it's content. If the content is hidden, the state
 * of child components isn't lost.
 */
public class HidingComponent extends Component<HidingComponent> {
    Runnable content;
    boolean showHiddenValidation;
    boolean hidden;

    static class Template extends ComponentTemplateBase<HidingComponent> {

        @Override
        public void doRender(HidingComponent component, RiseCanvas<?> html) {
            if (!component.isHidden() && component.getContent() != null)
                component.getContent().run();
        }
    }

    public HidingComponent() {
    }

    public HidingComponent(Runnable content) {
        this.content = content;
    }

    public HidingComponent content(Runnable content) {
        this.content = content;
        return this;
    }

    public HidingComponent showHiddenValidation() {
        showHiddenValidation = true;
        return this;
    }

    public HidingComponent showHiddenValidation(boolean value) {
        showHiddenValidation = value;
        return this;
    }

    public boolean isShowHiddenValidation() {
        return showHiddenValidation;
    }

    public Runnable getContent() {
        return content;
    }

    public HidingComponent hide() {
        this.hidden = true;
        return this;
    }

    public HidingComponent hidden(boolean value) {
        this.hidden = value;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }
}
