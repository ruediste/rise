package com.github.ruediste.rise.component.components;

import java.awt.event.ComponentEvent;
import java.util.Deque;
import java.util.LinkedList;

import com.github.ruediste.rise.component.fragment.HtmlFragment;
import com.github.ruediste.rise.component.fragment.HtmlFragment.EventHandlingOutcome;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;

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
@DefaultTemplate(CComponentStackTemplate.class)
public class CComponentStack extends ComponentBase<CComponentStack> {

    Deque<HtmlFragment> stack = new LinkedList<>();

    /**
     * Pop the top component from the next containing {@link CComponentStack}
     */
    public static class PopComponentEvent {

    }

    /**
     * Push a component to the next containing {@link CComponentStack}
     */
    public static class PushComponentEvent {

        final private HtmlFragment fragment;

        public PushComponentEvent(HtmlFragment fragment) {
            this.fragment = fragment;
        }

        public HtmlFragment getFragment() {
            return fragment;
        }
    }

    HtmlFragment containerFragment;

    public CComponentStack() {
        containerFragment = new HtmlFragment();
        containerFragment.register(PopComponentEvent.class, e -> {
            pop();
            return EventHandlingOutcome.HANDLED;
        });
        containerFragment.register(PushComponentEvent.class, e -> {
            push(e.getFragment());
            return EventHandlingOutcome.HANDLED;
        });
    }

    public CComponentStack(HtmlFragment fragment) {
        this();
        push(fragment);
    }

    public HtmlFragment peek() {
        return stack.peek();
    }

    /**
     * @return the component stack (this)
     */
    public CComponentStack push(HtmlFragment e) {
        stack.push(e);
        e.setParent(containerFragment);
        return this;
    }

    public HtmlFragment pop() {
        HtmlFragment tos = stack.pop();
        tos.setParent(null);
        return tos;
    }

    public HtmlFragment getContainerFragment() {
        return containerFragment;
    }

    public static void raisePop(HtmlFragment start) {
        start.raiseEventBubbling(new CComponentStack.PopComponentEvent());
    }

    public static void raisePush(HtmlFragment start, HtmlFragment fragmentToPush) {
        start.raiseEventBubbling(new CComponentStack.PushComponentEvent(fragmentToPush));
    }
}
