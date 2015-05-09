package com.github.ruediste.rise.component.web.components.template;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.web.components.CRender;

public class CRenderHtmlTemplate extends CWTemplateBase<CRender> {

	@Override
	public void render(CRender component, HtmlCanvas html) throws IOException {
		component.getRenderer().accept(html);
	}
}
