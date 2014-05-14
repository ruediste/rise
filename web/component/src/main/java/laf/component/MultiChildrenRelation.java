package laf.component;

import java.util.*;

public class MultiChildrenRelation extends ChildRelation {

	private final ArrayList<Component> children = new ArrayList<>();

	public MultiChildrenRelation(ComponentBase parent) {
		super(parent);
	}

	@Override
	public Iterator<Component> iterator() {
		return children.iterator();
	}

	@Override
	public void childRemoved(Component child) {
		children.remove(child);
	}

	public void add(Component component) {
		children.add(component);
		postAdd(component);
	}

	public void add(int index, Component component) {
		children.add(index, component);
		postAdd(component);
	}

	public void remove(Component component) {
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
	public Collection<Component> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

}
