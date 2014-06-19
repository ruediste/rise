package laf.component.basic.html;

import java.io.IOException;

import laf.component.html.template.HtmlTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CRenderHtmlTemplate extends HtmlTemplateBase<CRender> {

	@Override
	public void render(CRender component, HtmlCanvas html,
			laf.component.html.RenderUtil util) throws IOException {
		component.render(html, util);
	}
}
