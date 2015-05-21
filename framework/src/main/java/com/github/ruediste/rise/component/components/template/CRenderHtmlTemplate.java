package com.github.ruediste.rise.component.components.template;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.components.CRender;

public class CRenderHtmlTemplate extends ComponentTemplateBase<CRender> {

	@Override
	public void doRender(CRender component, HtmlCanvas html) throws IOException {
		component.getRenderer().accept(html);
	}
}
