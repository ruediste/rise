package com.github.ruediste.rise.component.tree;

import java.util.Iterator;

import com.google.common.collect.Iterators;

/**
 * Manages a relation referencing a single child
 */
public class SingleChildRelation<TChild extends Component, TContainingComponent extends ComponentBase<TContainingComponent>>
		extends ChildRelation<TContainingComponent> {

	private TChild child;

	public SingleChildRelation(TContainingComponent containingComponent) {
		super(containingComponent);
	}

	public Component getChild() {
		return child;
	}

	public TChild setChild(TChild newChild) {
		if (child != null && child.getParent() != null) {
			child.getParent().childRemoved(child);
			child.parentChanged(null);
		}

		child = newChild;

		if (child != null) {
			child.parentChanged(containingComponent);
		}
		return newChild;
	}

	@Override
	public void childRemoved(Component child) {
		if (this.child == child) {
			child = null;
		}
	}

	@Override
	public Iterator<Component> iterator() {
		if (child == null) {
			return Iterators.emptyIterator();
		}
		return Iterators.<Component> singletonIterator(child);
	}
}
