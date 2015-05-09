package com.github.ruediste.rise.component.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Relation of a component to multiple children.
 */
public class MultiChildrenRelation<TChild extends Component, TContainingComponent extends ComponentBase<TContainingComponent>>
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
	 */
	public void remove(TChild component) {
		children.remove(component);
		postRemove(component);
	}

	/**
	 * remove a child
	 */
	public void remove(int index) {
		postRemove(children.remove(index));
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

	/**
	 * Return an unmodifiable view to the children of this relation
	 */
	public Collection<TChild> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

}
