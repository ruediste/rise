package laf.component.web.basic.template;

import static org.rendersnake.HtmlAttributesFactory.*;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.basic.CReload;
import laf.component.web.CWRenderUtil;
import laf.component.web.CWTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CReloadHtmlTemplate extends CWTemplateBase<CReload> {
	@Inject
	CWRenderUtil util;

	@Override
	public void render(CReload component, HtmlCanvas html) throws IOException {
		html.form(xmlns("http://www.w3.org/1999/xhtml").class_("c_reload"))
		.span(class_("_componentId c_hidden"))
		.content(String.valueOf(util.getComponentId()));
		super.render(component, html);
		html._form();
	}
}
