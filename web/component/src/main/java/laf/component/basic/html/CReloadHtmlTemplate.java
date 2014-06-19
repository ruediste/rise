package laf.component.basic.html;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.xmlns;

import java.io.IOException;

import laf.component.basic.CReload;
import laf.component.html.RenderUtil;
import laf.component.html.template.HtmlTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CReloadHtmlTemplate extends HtmlTemplateBase<CReload> {

	@Override
	public void render(CReload component, HtmlCanvas html, RenderUtil util)
			throws IOException {
		html.form(xmlns("http://www.w3.org/1999/xhtml").class_("c_reload"))
				.span(class_("_componentId c_hidden"))
				.content(String.valueOf(util.getComponentId()));
		super.render(component, html, util);
		html._form();
	}
}
