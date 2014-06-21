package laf.component.tree;

import java.util.Iterator;

import com.google.common.collect.Iterators;

public class SingleChildRelation<T extends Component, TSelf extends ComponentBase<TSelf>>
extends ChildRelation<TSelf> {

	private T child;

	public SingleChildRelation(TSelf parent) {
		super(parent);
	}

	public Component getChild() {
		return child;
	}

	public T setChild(T newChild) {
		if (child != null && child.getParent() != null) {
			child.getParent().childRemoved(child);
			child.parentChanged(null);
		}

		child = newChild;

		if (child != null) {
			child.parentChanged(parent);
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
