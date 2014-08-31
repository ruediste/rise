package laf.component.web.basic.template;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.web.api.CWRenderUtil;
import laf.component.web.api.CWTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CRenderHtmlTemplate extends CWTemplateBase<CRender> {
	@Inject
	CWRenderUtil util;

	@Override
	public void render(CRender component, HtmlCanvas html) throws IOException {
		component.render(html, util);
	}
}
