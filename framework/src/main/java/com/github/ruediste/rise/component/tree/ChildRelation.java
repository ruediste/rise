package com.github.ruediste.rise.component.tree;

/**
 * Base class of child relations. see {@link ComponentBase} for details
 *
 * @param <TContainingComponent>
 *            type of the containing component
 */
public abstract class ChildRelation<TContainingComponent extends ComponentBase<TContainingComponent>>
        implements Iterable<Component> {

    protected TContainingComponent containingComponent;

    public ChildRelation(TContainingComponent containingComponent) {
        this.containingComponent = containingComponent;
        containingComponent.addChildRelation(this);
    }

    abstract public void childRemoved(Component child);
}
