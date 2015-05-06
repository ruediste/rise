package com.github.ruediste.laf.component.web.components;

import com.github.ruediste.laf.component.tree.ComponentBase;
import com.github.ruediste.laf.component.web.components.template.CRenderHtmlTemplate;

@DefaultTemplate(CRenderHtmlTemplate.class)
public class CRender extends ComponentBase<CRender> {

	private final Renderer renderer;

	public CRender(Renderer renderer) {
		this.renderer = renderer;
	}

	public Renderer getRenderer() {
		return renderer;
	}
}
