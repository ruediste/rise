package laf.component.basic.html;

import java.io.IOException;

import laf.component.basic.CText;
import laf.component.html.RenderUtil;
import laf.component.html.template.HtmlTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CTextHtmlTemplate extends HtmlTemplateBase<CText> {

	@Override
	public void render(CText component, HtmlCanvas html, RenderUtil util)
			throws IOException {
		html.write(component.getText());
	}

}
