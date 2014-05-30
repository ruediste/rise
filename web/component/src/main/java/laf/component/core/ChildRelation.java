package laf.component.core;


public abstract class ChildRelation<TSelf extends ComponentBase<?>> implements
		Iterable<Component> {

	protected TSelf parent;

	public ChildRelation(TSelf parent) {
		this.parent = parent;
		parent.addChildRelation(this);
	}

	abstract public void childRemoved(Component child);
}
