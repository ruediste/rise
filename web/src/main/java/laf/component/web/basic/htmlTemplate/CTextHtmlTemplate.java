package laf.component.web.basic.htmlTemplate;

import java.io.IOException;

import laf.component.core.basic.CText;
import laf.component.web.api.CWRenderUtil;
import laf.component.web.api.CWTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CTextHtmlTemplate extends CWTemplateBase<CText> {

	@Override
	public void render(CText component, HtmlCanvas html, CWRenderUtil util)
			throws IOException {
		html.write(component.getText());
	}

}
