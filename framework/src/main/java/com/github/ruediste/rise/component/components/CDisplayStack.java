package com.github.ruediste.rise.component.components;

import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.CHide;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

/**
 * Displays the {@link Runnable}s of the {@link DisplayStack}. Push or pop
 * runnables by using {@link ComponentUtil#push(Runnable)} an
 * {@link ComponentUtil#pop()}
 */
public class CDisplayStack extends Component<CDisplayStack> {

    boolean isTransient;

    Runnable fallback;

    static class Template extends ComponentTemplateBase<CDisplayStack> {

        @Inject
        DisplayStack stack;

        @Override
        public void doRender(CDisplayStack component, RiseCanvas<?> html) {
            ArrayDeque<Runnable> stack = this.stack.getStack();
            ArrayList<Runnable> stackList = new ArrayList<>(stack);
            if (component.fallback != null) {
                html.add(new CHide().key(component.fallback).transient_(component.isTransient)
                        .content(component.fallback).hidden(!stackList.isEmpty()));
            }
            for (int i = 0; i < stackList.size(); i++) {
                Runnable item = stackList.get(i);
                html.add(new CHide().key(item).hidden(i != 0).transient_(component.isTransient).content(item));
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

}
