package com.github.ruediste.rise.component.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.github.ruediste.attachedProperties4J.AttachedProperty;

/**
 * Static utility class for component tree traversal.
 *
 */
public class ComponentTreeUtil {

    private ComponentTreeUtil() {

    }

    /**
     * Return all components in the sub tree rooted in the given component. That
     * is the component itself and all children, transitively.
     */
    static public List<Component> subTree(Component c) {
        ArrayList<Component> result = new ArrayList<>();
        subTree(c, result);
        return result;
    }

    static private void subTree(Component c, ArrayList<Component> result) {
        result.add(c);
        for (Component child : c.getChildren()) {
            subTree(child, result);
        }
    }

    /**
     * Returns the ancestors of the given {@link Component}, up to and including
     * the root component.
     *
     * @param includeStartComponent
     *            if true, start with the provided component, otherwise start
     *            with it's parent
     */
    static public List<Component> ancestors(Component start,
            boolean includeStartComponent) {
        ArrayList<Component> result = new ArrayList<>();
        Component c;
        if (includeStartComponent) {
            c = start;
        } else {
            c = start.getParent();
        }
        while (c != null) {
            result.add(c);
            c = c.getParent();
        }
        return result;
    }

    /**
     * Returns the path from the root component to the target component.
     *
     * @param includeTargetComponent
     *            if true, include the target component, otherwise stop at the
     *            target component's parent
     */
    static public Collection<Component> path(Component target,
            boolean includeTargetComponent) {
        List<Component> result = ancestors(target, includeTargetComponent);
        Collections.reverse(result);
        return result;
    }

    static private class EventRegistration {
        EventRegistration(Consumer<?> listener, Class<?> eventType,
                boolean handlesToo) {
            super();
            this.listener = listener;
            this.eventType = eventType;
            this.handlesToo = handlesToo;
        }

        Consumer<?> listener;
        Class<?> eventType;
        boolean handlesToo;
    }

    static private AttachedProperty<Component, List<EventRegistration>> listenerProperty = new AttachedProperty<>(
            "componentEventListeners");

    static public void raiseEvent(Component target, ComponentEvent event) {
        event.getType().getComponentsToVisit(target)
                .forEach(c -> raiseEventOn(c, event));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void raiseEventOn(Component target, ComponentEvent event) {
        // create copy of listeners
        List<EventRegistration> listeners;
        {
            List<EventRegistration> tmp = getEventListeners(target);
            synchronized (tmp) {
                listeners = new ArrayList<>(tmp);
            }
        }

        // raise events
        for (EventRegistration registration : listeners) {
            if (registration.handlesToo || !event.isCanceled()) {
                if (registration.eventType.isAssignableFrom(event.getClass())) {
                    ((Consumer) registration.listener).accept(event);
                }
            }
        }
    }

    private static List<EventRegistration> getEventListeners(Component target) {
        return listenerProperty.setIfAbsent(target, () -> new ArrayList<>());
    }

    static public <T> void registerEventListener(Component component,
            Class<T> eventType, Consumer<T> listener) {
        registerEventListener(eventType, component, listener, false);
    }

    /**
     * register an event listener
     * 
     * @param handlesToo
     *            if set to true, the listener will be notified even for events
     *            which are already canceled
     */
    static public <T> void registerEventListener(Class<?> eventType,
            Component component, Consumer<T> listener, boolean handlesToo) {
        List<EventRegistration> listeners = getEventListeners(component);
        synchronized (listeners) {
            listeners.add(
                    new EventRegistration(listener, eventType, handlesToo));
        }
    }
}
