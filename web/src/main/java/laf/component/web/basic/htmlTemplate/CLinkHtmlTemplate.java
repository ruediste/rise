package laf.component.web.basic.htmlTemplate;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import laf.component.web.api.CWRenderUtil;
import laf.component.web.api.CWTemplateBase;
import laf.component.web.basic.CLink;

import org.rendersnake.HtmlCanvas;

public class CLinkHtmlTemplate extends CWTemplateBase<CLink> {

	@Override
	public void render(CLink component, HtmlCanvas html, CWRenderUtil util)
			throws IOException {
		html.a(href(util.url(component.getDestination())));
		super.render(component, html, util);
		html._a();
	}
}
