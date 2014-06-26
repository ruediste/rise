package laf.component.basic.htmlTemplate;

import java.io.IOException;

import laf.component.basic.CGroup;
import laf.component.html.RenderUtil;
import laf.component.html.template.HtmlTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CGroupHtmlTemplate extends HtmlTemplateBase<CGroup> {

	@Override
	public void render(CGroup component, HtmlCanvas html, RenderUtil util)
			throws IOException {
		html.div();
		super.render(component, html, util);
		html._div();
	}
}
