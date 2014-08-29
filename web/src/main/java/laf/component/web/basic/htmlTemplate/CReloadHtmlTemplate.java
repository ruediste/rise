package laf.component.web.basic.htmlTemplate;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.xmlns;

import java.io.IOException;

import laf.component.core.basic.CReload;
import laf.component.web.api.CWRenderUtil;
import laf.component.web.api.CWTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CReloadHtmlTemplate extends CWTemplateBase<CReload> {

	@Override
	public void render(CReload component, HtmlCanvas html, CWRenderUtil util)
			throws IOException {
		html.form(xmlns("http://www.w3.org/1999/xhtml").class_("c_reload"))
				.span(class_("_componentId c_hidden"))
				.content(String.valueOf(util.getComponentId()));
		super.render(component, html, util);
		html._form();
	}
}
