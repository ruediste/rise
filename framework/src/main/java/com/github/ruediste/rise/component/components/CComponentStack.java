package com.github.ruediste.rise.component.components;

import java.awt.event.ComponentEvent;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.component.tree.Component.EventHandlingOutcome;

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

    Deque<Component> stack = new LinkedList<>();

    /**
     * Pop the top component from the next containing {@link CComponentStack}
     */
    public static class PopComponentEvent {

    }

    /**
     * Push a component to the next containing {@link CComponentStack}
     */
    public static class PushComponentEvent {

        final private Component fragment;

        public PushComponentEvent(Component fragment) {
            this.fragment = fragment;
        }

        public Component getFragment() {
            return fragment;
        }
    }

    Component containerFragment;

    public CComponentStack() {
        containerFragment = new Component() {
            @Override
            protected void produceHtml(HtmlConsumer consumer) {
                Component peek = stack.peek();
                if (peek != null)
                    peek.getHtmlProducer().produce(consumer);
            }

            @Override
            public Iterable<Component> getChildren() {
                if (stack.isEmpty())
                    return Collections.emptyList();
                return Collections.singleton(stack.peek());
            }
        };
        containerFragment.register(PopComponentEvent.class, e -> {
            pop();
            return EventHandlingOutcome.HANDLED;
        });
        containerFragment.register(PushComponentEvent.class, e -> {
            push(e.getFragment());
            return EventHandlingOutcome.HANDLED;
        });
    }

    public CComponentStack(Component fragment) {
        this();
        push(fragment);
    }

    public Component peek() {
        return stack.peek();
    }

    /**
     * @return the component stack (this)
     */
    public CComponentStack push(Component e) {
        stack.push(e);
        e.setParent(containerFragment);
        return this;
    }

    public Component pop() {
        Component tos = stack.pop();
        tos.setParent(null);
        return tos;
    }

    public Component getContainerFragment() {
        return containerFragment;
    }

    public static void raisePop(Component start) {
        start.raiseEventBubbling(new CComponentStack.PopComponentEvent());
    }

    public static void raisePush(Component start, Component fragmentToPush) {
        start.raiseEventBubbling(new CComponentStack.PushComponentEvent(fragmentToPush));
    }
}
