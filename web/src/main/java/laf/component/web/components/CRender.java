package laf.component.web.components;

import laf.component.core.tree.ComponentBase;

public class CRender extends ComponentBase<CRender> {

	private final Renderer renderer;

	public CRender(Renderer renderer) {
		this.renderer = renderer;
	}

	public Renderer getRenderer() {
		return renderer;
	}
}
