package laf.component.web.basic.htmlTemplate;

import java.io.IOException;

import laf.component.web.api.CWTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CRenderHtmlTemplate extends CWTemplateBase<CRender> {

	@Override
	public void render(CRender component, HtmlCanvas html,
			laf.component.web.api.CWRenderUtil util) throws IOException {
		component.render(html, util);
	}
}
