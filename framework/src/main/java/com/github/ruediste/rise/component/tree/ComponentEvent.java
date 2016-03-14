package com.github.ruediste.rise.component.tree;

import java.util.Collections;

/**
 * Base interface for component events.
 * <p>
 * Depending on the sub interface implemented, the event visits the component
 * tree. For each component, listeners can be registered which are notified.
 */
public interface ComponentEvent {
    public enum ComponentEventType {
        /**
         * The event traverses the tree towards the root, starting with the
         * target component
         */
        BUBBLE {
            @Override
            public Iterable<Component> getComponentsToVisit(Component target) {
                return ComponentTreeUtil.ancestors(target, true);
            }
        },

        /**
         * The event traverses the tree towards the target component, starting
         * with the root
         */
        TUNNEL {
            @Override
            public Iterable<Component> getComponentsToVisit(Component target) {
                return ComponentTreeUtil.path(target, true);
            }
        },

        /**
         * The event directly visits the target component
         */
        DIRECT {
            @Override
            public Iterable<Component> getComponentsToVisit(Component target) {
                return Collections.singleton(target);
            }
        },

        /**
         * The event visits all components in the
         * {@link ComponentTreeUtil#subTree(Component)} of the target component
         */
        BROADCAST {
            @Override
            public Iterable<Component> getComponentsToVisit(Component target) {
                return ComponentTreeUtil.subTree(target);
            }
        };

        public abstract Iterable<Component> getComponentsToVisit(Component target);
    }

    ComponentEvent.ComponentEventType getType();

    boolean isCanceled();

    /**
     * Cancel this event. No more listeners will be notified.
     */
    void cancel();
}