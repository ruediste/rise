package laf.component.web.components.template;

import java.io.IOException;

import laf.component.web.CWTemplateBase;
import laf.component.web.components.CForm;

import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;

public class CFormHtmlTemplate extends CWTemplateBase<CForm> {

	@Override
	public void render(CForm component, HtmlCanvas html) throws IOException {
		html.form(HtmlAttributesFactory.add("role", "form", true));
		super.render(component, html);
		html._form();
	}
}
