package com.github.ruediste.laf.component.web.components;

import com.github.ruediste.laf.component.tree.Component;
import com.github.ruediste.laf.component.tree.ComponentBase;
import com.github.ruediste.laf.component.tree.SingleChildRelation;

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
