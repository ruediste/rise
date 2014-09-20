package laf.component.core.basic;

import laf.component.web.basic.template.CRender;
import laf.component.web.basic.template.Renderer;

public class CGroup extends MultiChildrenComponent<CGroup> {

	public CGroup render(Renderer renderer) {
		children.add(new CRender(renderer));
		return self();
	}
}
