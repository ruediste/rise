package laf.component.basic.html;

import java.io.IOException;

import laf.component.basic.CTextField;
import laf.component.html.ApplyValuesUtil;
import laf.component.html.RenderUtil;
import laf.component.html.template.HtmlTemplateBase;

import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;

public class CTextFieldHtmlTemplate extends HtmlTemplateBase<CTextField> {

	@Override
	public void render(CTextField component, HtmlCanvas html, RenderUtil util)
			throws IOException {
		html.input(HtmlAttributesFactory.type("text")
				.value(component.getValue()).name(util.getKey("value")));
	}

	@Override
	public void applyValues(CTextField component, ApplyValuesUtil util) {
		component.setValue(util.getValue("value"));
	}
}
