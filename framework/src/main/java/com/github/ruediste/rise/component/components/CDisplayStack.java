package com.github.ruediste.rise.component.components;

import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.HidingComponent;
import com.github.ruediste.rise.integration.RiseCanvas;

/**
 * Displays the {@link Runnable}s of the {@link DisplayStack}. Push or pop
 * runnables by using {@link ComponentUtil#push(Runnable)} an
 * {@link ComponentUtil#pop()}
 */
public class CDisplayStack extends Component<CDisplayStack> {

    boolean isTransient;

    private boolean showHiddenValidation;

    Runnable fallback;

    static class Template extends ComponentTemplateBase<CDisplayStack> {

        @Inject
        DisplayStack stack;

        @Override
        public void doRender(CDisplayStack component, RiseCanvas<?> html) {
            ArrayDeque<Runnable> stack = this.stack.getStack();
            if (component.isTransient) {
                if (!stack.isEmpty())
                    html.render(stack.peek());
                else if (component.fallback != null)
                    component.fallback.run();

            } else {
                ArrayList<Runnable> stackList = new ArrayList<>(stack);
                if (stackList.isEmpty()) {
                    if (component.fallback != null)
                        html.add(new HidingComponent().key(component.fallback)
                                .showHiddenValidation(component.isShowHiddenValidation()).content(component.fallback));
                } else {
                    for (int i = 0; i < stackList.size(); i++) {
                        Runnable item = stackList.get(i);
                        html.add(new HidingComponent().key(item).hidden(i != 0)
                                .showHiddenValidation(component.isShowHiddenValidation()).content(item));
                    }
                }
            }
        }

    }

    public CDisplayStack() {
    }

    public CDisplayStack(Runnable fallback) {
        this.fallback = fallback;
    }

    /**
     * Make the stack transient, such that only the state of the top of the
     * stack survives a page reload.
     */
    public CDisplayStack setTransient() {
        isTransient = true;
        return this;
    }

    public boolean isShowHiddenValidation() {
        return showHiddenValidation;
    }

    public CDisplayStack setShowHiddenValidation(boolean showHiddenValidation) {
        this.showHiddenValidation = showHiddenValidation;
        return this;
    }
}
