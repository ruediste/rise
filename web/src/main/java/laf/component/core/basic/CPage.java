package laf.component.core.basic;

import laf.component.core.tree.*;

public class CPage extends ComponentBase<CPage> {
	private final SingleChildRelation<Component, CPage> child = new SingleChildRelation<Component, CPage>(
			this);

	private CReload reload = new CReload();

	public CPage() {
		child.setChild(reload);
	}

	public CPage add(Component c) {
		reload.children.add(c);
		return this;
	}
}
