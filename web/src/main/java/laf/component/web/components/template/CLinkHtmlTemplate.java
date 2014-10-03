package laf.component.web.components.template;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.web.CWRenderUtil;
import laf.component.web.CWTemplateBase;
import laf.component.web.components.CLink;

import org.rendersnake.HtmlCanvas;

public class CLinkHtmlTemplate extends CWTemplateBase<CLink> {
	@Inject
	CWRenderUtil util;

	@Override
	public void render(CLink component, HtmlCanvas html) throws IOException {
		html.a(href(component.getDestinationUrl()));
		super.render(component, html);
		html._a();
	}
}
