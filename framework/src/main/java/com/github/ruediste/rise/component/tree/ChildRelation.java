package com.github.ruediste.rise.component.tree;

/**
 * Base class of child relations.
 * <p>
 * <img src="doc-files/childRelation.png" alt="">
 * 
 * <p>
 * The child relation takes care of updating the bidirectional relation between
 * children and parents. Putting this logic into a separate class makes reuse
 * simple.
 * 
 * @param <TContainingComponent>
 *            type of the containing component
 * 
 * @see RelationsComponent
 */
public abstract class ChildRelation<TContainingComponent extends RelationsComponent<TContainingComponent>>
        implements Iterable<Component> {

    protected TContainingComponent containingComponent;

    public ChildRelation(TContainingComponent containingComponent) {
        this.containingComponent = containingComponent;
        containingComponent.addChildRelation(this);
    }

    abstract public void childRemoved(Component child);
}
