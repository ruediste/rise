package laf.component;

public abstract class ChildRelation implements Iterable<Component> {

	protected ComponentBase parent;

	public ChildRelation(ComponentBase parent) {
		this.parent = parent;
		parent.addChildRelation(this);
	}

	abstract public void childRemoved(Component child);
}
