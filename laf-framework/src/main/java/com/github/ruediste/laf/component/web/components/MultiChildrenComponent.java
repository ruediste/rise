package com.github.ruediste.laf.component.web.components;

import com.github.ruediste.laf.component.tree.Component;
import com.github.ruediste.laf.component.tree.ComponentBase;
import com.github.ruediste.laf.component.tree.MultiChildrenRelation;

public class MultiChildrenComponent<TSelf extends ComponentBase<TSelf>> extends
		ComponentBase<TSelf> {
	public final MultiChildrenRelation<Component, TSelf> children = new MultiChildrenRelation<>(
			self());

	public TSelf add(Component child) {
		return children.add(child);
	}
}
