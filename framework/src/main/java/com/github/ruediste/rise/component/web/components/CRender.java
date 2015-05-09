package com.github.ruediste.rise.component.web.components;

import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.component.web.components.template.CRenderHtmlTemplate;

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
