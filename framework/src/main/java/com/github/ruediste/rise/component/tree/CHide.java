package com.github.ruediste.rise.component.tree;

import com.github.ruediste.rise.component.components.ComponentTemplateBase;
import com.github.ruediste.rise.integration.RiseCanvas;

/**
 * Component optionally hiding it's content. If the content is hidden, the state
 * of child components isn't lost.
 */
public class CHide extends Component<CHide> {
    private Runnable content;
    private boolean isTransient;
    private boolean hidden;

    static class Template extends ComponentTemplateBase<CHide> {

        @Override
        public void doRender(CHide component, RiseCanvas<?> html) {
            if (component.getContent() != null) {
                if (component.isHidden()) {
                    if (!component.isTransient()) {
                        // hidden and non-transient, run without output
                        html.internal_target().suspendOutput(true);
                        try {
                            component.getContent().run();
                        } finally {
                            html.internal_target().suspendOutput(false);
                        }
                    }
                } else
                    component.getContent().run();
            }
        }
    }

    public CHide() {
    }

    public CHide(Runnable content) {
        this.content = content;
    }

    public CHide content(Runnable content) {
        this.content = content;
        return this;
    }

    public Runnable getContent() {
        return content;
    }

    public CHide hide() {
        this.hidden = true;
        return this;
    }

    public CHide hidden(boolean value) {
        this.hidden = value;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public CHide transient_(boolean isTransient) {
        this.isTransient = isTransient;
        return this;
    }

}
