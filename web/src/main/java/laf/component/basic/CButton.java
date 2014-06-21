package laf.component.basic;

import laf.component.core.*;
import laf.component.tree.Component;
import laf.component.tree.ComponentBase;
import laf.component.tree.MultiChildrenRelation;

public class CButton extends ComponentBase<CButton> {
	public MultiChildrenRelation<Component, CButton> children = new MultiChildrenRelation<>(
			this);
	private Runnable handler;

	public CButton() {
	}

	public CButton(String text) {
		children.add(new CText(text));
	}

	CButton withHandler(Runnable handler) {
		this.handler = handler;
		return this;
	}

	public Runnable getHandler() {
		return handler;
	}

}
