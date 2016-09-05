package com.github.ruediste.rise.component.components;

import java.awt.event.ComponentEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;

import com.github.ruediste.rise.component.render.ComponentState;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.HidingComponent;
import com.github.ruediste.rise.integration.RiseCanvas;

/**
 * Contains a stack of {@link Component}s. The components can be pushed and
 * popped directly using instance methods, or by
 * {@link ComponentTreeUtil#raiseEvent(Component, ComponentEvent) raising}
 * {@link PopComponentEvent}s or {@link PushComponentEvent}s from child
 * components.
 * 
 * <p>
 * In addition, a {@link ComponentStackHandle} can be created from any
 * component, encapsulating the event raising.
 */
public class CComponentStack extends Component<CComponentStack> {

    @ComponentState
    ArrayDeque<Runnable> stack = new ArrayDeque<>();

    boolean isTransient;
    private boolean showHiddenValidation;

    static class Template extends ComponentTemplateBase<CComponentStack> {

        @Override
        public void doRender(CComponentStack component, RiseCanvas<?> html) {
            if (!component.isTransient) {
                ArrayList<Runnable> stack = new ArrayList<>(component.stack);
                for (int i = 0; i < stack.size(); i++) {
                    Runnable item = stack.get(i);
                    html.add(new HidingComponent().key(item).hidden(i != 0)
                            .showHiddenValidation(component.isShowHiddenValidation()).content(item));
                }
            } else if (!component.stack.isEmpty())
                html.render(component.stack.peek());
        }

    }

    /**
     * Make the stack transient, such that only the state of the top of the
     * stack survives a page reload.
     */
    public CComponentStack setTransient() {
        isTransient = true;
        return this;
    }

    public CComponentStack push(Runnable fragment) {
        stack.push(fragment);
        return this;
    }

    public void pop() {
        stack.pop();
    }

    public boolean isShowHiddenValidation() {
        return showHiddenValidation;
    }

    public CComponentStack setShowHiddenValidation(boolean showHiddenValidation) {
        this.showHiddenValidation = showHiddenValidation;
        return this;
    }

    /**
     * Pop the top component from the next containing {@link CComponentStack}
     */
    public static class PopComponentEvent {

    }

    /**
     * Push a component to the next containing {@link CComponentStack}
     */
    public static class PushComponentEvent {

        final private Runnable fragment;

        public PushComponentEvent(Runnable fragment) {
            this.fragment = fragment;
        }

        public Runnable getFragment() {
            return fragment;
        }
    }

    public CComponentStack() {
        register(PopComponentEvent.class, e -> {
            pop();
            return EventHandlingOutcome.HANDLED;
        });
        register(PushComponentEvent.class, e -> {
            push(e.getFragment());
            return EventHandlingOutcome.HANDLED;
        });
    }

    public CComponentStack(Runnable fragment) {
        this();
        push(fragment);
    }

    public Runnable peek() {
        return stack.peek();
    }

    public static void raisePop(Component<?> start) {
        start.raiseEventBubbling(new CComponentStack.PopComponentEvent());
    }

    public static void raisePush(Component<?> start, Runnable fragmentToPush) {
        start.raiseEventBubbling(new CComponentStack.PushComponentEvent(fragmentToPush));
    }
}
