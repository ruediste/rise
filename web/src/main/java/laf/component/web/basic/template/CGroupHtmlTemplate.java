package laf.component.web.basic.template;

import java.io.IOException;

import laf.component.core.basic.CGroup;
import laf.component.web.api.CWTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CGroupHtmlTemplate extends CWTemplateBase<CGroup> {

	@Override
	public void render(CGroup component, HtmlCanvas html)
			throws IOException {
		html.div();
		super.render(component, html);
		html._div();
	}
}
