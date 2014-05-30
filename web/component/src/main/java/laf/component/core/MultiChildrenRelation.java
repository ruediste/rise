package laf.component.core;

import java.util.*;

public class MultiChildrenRelation<T extends Component, TSelf extends ComponentBase<TSelf>>
extends ChildRelation<TSelf> {

	private final ArrayList<T> children = new ArrayList<>();

	public MultiChildrenRelation(TSelf parent) {
		super(parent);
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

	public TSelf add(T component) {
		children.add(component);
		postAdd(component);
		return parent;
	}

	public TSelf add(int index, T component) {
		children.add(index, component);
		postAdd(component);
		return parent;
	}

	public void remove(T component) {
		children.remove(component);
		postRemove(component);
	}

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
		component.parentChanged(parent);
	}

	/**
	 * Returns an unmodifiable view to the children of this relation
	 */
	public Collection<T> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

}
