package laf.component.basic.htmlTemplate;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import laf.component.basic.CLink;
import laf.component.html.RenderUtil;
import laf.component.html.template.HtmlTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CLinkHtmlTemplate extends HtmlTemplateBase<CLink> {

	@Override
	public void render(CLink component, HtmlCanvas html, RenderUtil util)
			throws IOException {
		html.a(href(util.url(component.getDestination())));
		super.render(component, html, util);
		html._a();
	}
}
