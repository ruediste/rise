package com.github.ruediste.laf.component.web.components.template;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.component.web.CWRenderUtil;
import com.github.ruediste.laf.component.web.CWTemplateBase;
import com.github.ruediste.laf.component.web.components.CRender;

public class CRenderHtmlTemplate extends CWTemplateBase<CRender> {
	@Inject
	CWRenderUtil util;

	@Override
	public void render(CRender component, HtmlCanvas html) throws IOException {
		component.getRenderer().accept(html);
	}
}
