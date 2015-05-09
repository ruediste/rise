package com.github.ruediste.rise.component.web.components;

import com.github.ruediste.rise.component.web.components.template.CGroupHtmlTemplate;

@DefaultTemplate(CGroupHtmlTemplate.class)
public class CGroup extends MultiChildrenComponent<CGroup> {

	public CGroup render(Renderer renderer) {
		children.add(new CRender(renderer));
		return self();
	}
}
