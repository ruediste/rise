package laf.component.web.basic.template;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.web.api.CWRenderUtil;
import laf.component.web.api.CWTemplateBase;
import laf.component.web.basic.CLink;

import org.rendersnake.HtmlCanvas;

public class CLinkHtmlTemplate extends CWTemplateBase<CLink> {
	@Inject
	CWRenderUtil util;

	@Override
	public void render(CLink component, HtmlCanvas html) throws IOException {
		html.a(href(util.url(component.getDestination())));
		super.render(component, html);
		html._a();
	}
}
