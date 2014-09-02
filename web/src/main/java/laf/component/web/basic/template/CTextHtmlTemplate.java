package laf.component.web.basic.template;

import java.io.IOException;

import laf.component.core.basic.CText;
import laf.component.web.CWTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CTextHtmlTemplate extends CWTemplateBase<CText> {

	@Override
	public void render(CText component, HtmlCanvas html)
			throws IOException {
		html.write(component.getText());
	}

}
