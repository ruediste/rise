package laf.component.web.basic;

import laf.component.core.basic.MultiChildrenComponent;

public class CGroup extends MultiChildrenComponent<CGroup> {

	public CGroup render(Renderer renderer) {
		children.add(new CRender(renderer));
		return self();
	}
}
