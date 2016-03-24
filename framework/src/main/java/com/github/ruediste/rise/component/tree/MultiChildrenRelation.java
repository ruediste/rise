package com.github.ruediste.rise.component.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Relation of a component to multiple children.
 * 
 * <p>
 * The children can either be given explicitely, or by binding them to a source
 * collection
 */
public class MultiChildrenRelation<TChild extends Component, TContainingComponent extends RelationsComponent<TContainingComponent>>
        extends ChildRelation<TContainingComponent> {

    private final ArrayList<TChild> children = new ArrayList<>();

    public MultiChildrenRelation(TContainingComponent containingComponent) {
        super(containingComponent);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Iterator<Component> iterator() {
        return (Iterator) children.iterator();
    }

    @Override
    public void childRemoved(Component child) {
        children.remove(child);
    }

    /**
     * Add the given component to the children referenced by this relation
     */
    public TContainingComponent add(TChild component) {
        children.add(component);
        postAdd(component);
        return containingComponent;
    }

    /**
     * Add the given component to the children referenced by this relation at a
     * given index
     */
    public TContainingComponent add(int index, TChild component) {
        children.add(index, component);
        postAdd(component);
        return containingComponent;
    }

    /**
     * remove a child
     * 
     * @return
     */
    public TContainingComponent remove(TChild component) {
        children.remove(component);
        postRemove(component);
        return containingComponent;
    }

    /**
     * Return an unmodifiable view to the children of this relation
     */
    public Collection<TChild> getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    /**
     * remove a child
     * 
     * @return
     */
    public TContainingComponent remove(int index) {
        postRemove(children.remove(index));
        return containingComponent;
    }

    private void postRemove(Component component) {
        component.parentChanged(null);
    }

    private void postAdd(Component component) {
        if (component.getParent() != null) {
            component.getParent().childRemoved(component);
        }
        component.parentChanged(containingComponent);
    }

}
