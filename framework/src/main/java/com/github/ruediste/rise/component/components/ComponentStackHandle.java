package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;

public class ComponentStackHandle {
	private Component component;

	public ComponentStackHandle(Component component) {
		this.component = component;
	}

	public void pushComponent(Component c) {
		ComponentTreeUtil.raiseEvent(component, new CComponentStack.PushComponentEvent(c));
	}

	public void popComponent() {
		ComponentTreeUtil.raiseEvent(component, new CComponentStack.PopComponentEvent());
	}
}
