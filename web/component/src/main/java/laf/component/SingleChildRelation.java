package laf.component;

import java.util.Iterator;

import com.google.common.collect.Iterators;

public class SingleChildRelation<T extends Component> extends ChildRelation {

	private T child;

	public SingleChildRelation(ComponentBase parent) {
		super(parent);
	}

	public Component getChild() {
		return child;
	}

	public void setChild(T newChild) {
		if (child != null && child.getParent() != null) {
			child.getParent().childRemoved(child);
			child.parentChanged(null);
		}

		child = newChild;

		if (child != null) {
			child.parentChanged(parent);
		}
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
