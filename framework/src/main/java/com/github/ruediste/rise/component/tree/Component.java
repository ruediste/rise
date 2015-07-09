package com.github.ruediste.rise.component.tree;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;

/**
 * Interface of all components
 */
public interface Component extends AttachedPropertyBearer {

    /**
     * Get the children of this component
     */
    Iterable<Component> getChildren();

    /**
     * Get the parent of this component. Can be null
     */
    Component getParent();

    /**
     * Notify this component that it's parent has been changed. When this method
     * is called, this component will already be returned from
     * {@link #getChildren()} of the new parent. It is the responsibility of the
     * caller to ensure that {@link #childRemoved(Component)} is called on the
     * old parent.
     */
    void parentChanged(Component newParent);

    /**
     * Notify this Component that the child has been removed. It is the
     * responsibility of the caller to call {@link #parentChanged(Component)} on
     * the child.
     */
    void childRemoved(Component child);

}
