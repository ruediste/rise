package com.github.ruediste.rise.component.components.template;

import java.io.IOException;

import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.components.CForm;

public class CFormHtmlTemplate extends ComponentTemplateBase<CForm> {

	@Override
	public void render(CForm component, HtmlCanvas html) throws IOException {
		html.form(HtmlAttributesFactory.add("role", "form", true));
		super.render(component, html);
		html._form();
	}
}
