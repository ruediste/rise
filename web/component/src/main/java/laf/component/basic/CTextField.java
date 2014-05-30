package laf.component.basic;

import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import laf.component.*;
import laf.component.core.ComponentBase;
import laf.component.html.ApplyValuesUtil;
import laf.component.html.RenderUtil;

import org.rendersnake.HtmlCanvas;

public class CTextField extends ComponentBase<CTextField> {

	String value;

	@Override
	public void applyValues(ApplyValuesUtil util) {
		value = util.getValue("value");
	}

	@Override
	public void render(HtmlCanvas html, RenderUtil util) throws IOException {
		html.input(type("text").value(value).name(util.getKey("value")));
	}
}
