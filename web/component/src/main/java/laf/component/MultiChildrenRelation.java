package laf.component;

import java.util.*;

public class MultiChildrenRelation<T extends Component> extends ChildRelation {

	private final ArrayList<T> children = new ArrayList<>();

	public MultiChildrenRelation(ComponentBase parent) {
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

	public void add(T component) {
		children.add(component);
		postAdd(component);
	}

	public void add(int index, T component) {
		children.add(index, component);
		postAdd(component);
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
