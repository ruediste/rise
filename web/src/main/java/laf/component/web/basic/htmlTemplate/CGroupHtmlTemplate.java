package laf.component.web.basic.htmlTemplate;

import java.io.IOException;

import laf.component.core.basic.CGroup;
import laf.component.web.api.CWRenderUtil;
import laf.component.web.api.CWTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CGroupHtmlTemplate extends CWTemplateBase<CGroup> {

	@Override
	public void render(CGroup component, HtmlCanvas html, CWRenderUtil util)
			throws IOException {
		html.div();
		super.render(component, html, util);
		html._div();
	}
}
