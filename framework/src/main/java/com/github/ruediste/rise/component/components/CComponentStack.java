package com.github.ruediste.rise.component.components;

import java.util.Deque;
import java.util.LinkedList;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.component.tree.ComponentEvent;
import com.github.ruediste.rise.component.tree.ComponentEventBase;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;

/**
 * Contains a stack of {@link Component}s. The components can be pushed and
 * popped directly using instance methods, or by
 * {@link ComponentTreeUtil#raiseEvent(Component, ComponentEvent) raising}
 * {@link PopComponentEvent}s or {@link PushComponentEvent}s from child
 * components.
 */
@DefaultTemplate(CComponentStackTemplate.class)
public class CComponentStack extends ComponentBase<CComponentStack> {

    Deque<Component> stack = new LinkedList<>();

    /**
     * Pop the top component from the next containing {@link CComponentStack}
     */
    public static class PopComponentEvent extends ComponentEventBase {

        public PopComponentEvent() {
            super(ComponentEventType.BUBBLE);
        }
    }

    /**
     * Push a component to the next containing {@link CComponentStack}
     */
    public static class PushComponentEvent extends ComponentEventBase {

        final private Component component;

        public PushComponentEvent(Component component) {
            super(ComponentEventType.BUBBLE);
            this.component = component;
        }

        public Component getComponent() {
            return component;
        }
    }

    public CComponentStack() {
        ComponentTreeUtil.registerEventListener(this, PopComponentEvent.class,
                e -> pop());
        ComponentTreeUtil.registerEventListener(this, PushComponentEvent.class,
                e -> push(e.getComponent()));
    }

    public CComponentStack(Component component) {
        this();
        push(component);
    }

    @Override
    public Iterable<Component> getChildren() {
        return stack;
    }

    @Override
    public void childRemoved(Component child) {
        stack.remove(child);
    }

    public Component peek() {
        return stack.peek();
    }

    /**
     * @return the component stack (this)
     */
    public CComponentStack push(Component e) {
        stack.push(e);
        if (e.getParent() != null)
            e.getParent().childRemoved(e);
        e.parentChanged(this);
        return this;
    }

    public Component pop() {
        Component tos = stack.pop();
        tos.parentChanged(null);
        return tos;
    }

}
