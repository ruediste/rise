package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.component.tree.MultiChildrenRelation;

public class MultiChildrenComponent<TSelf extends ComponentBase<TSelf>> extends
		ComponentBase<TSelf> {
	public final MultiChildrenRelation<Component, TSelf> children = new MultiChildrenRelation<>(
			self());

	public TSelf add(Component child) {
		return children.add(child);
	}
}
