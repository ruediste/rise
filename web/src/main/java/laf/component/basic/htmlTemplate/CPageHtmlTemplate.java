package laf.component.basic.htmlTemplate;

import java.io.IOException;

import laf.component.basic.CPage;
import laf.component.html.RenderUtil;
import laf.component.html.template.HtmlTemplateBase;

import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;

public class CPageHtmlTemplate extends HtmlTemplateBase<CPage> {

	@Override
	public void render(CPage component, HtmlCanvas html, RenderUtil util)
			throws IOException {

		// @formatter:off
		util.startHtmlPage(html);
		html.head()
		.title()
		.content("Yeah")
		.script(HtmlAttributesFactory.src(util
				.resourceUrl("js/jquery-1.11.1.js")))
				._script()
				.script(HtmlAttributesFactory.src(util
						.resourceUrl("js/components.js")))
						._script()
						.link(HtmlAttributesFactory.rel("stylesheet").type("text/css")
								.href(util.resourceUrl("css/components.css")))
								.script(HtmlAttributesFactory.type("text/javascript"))
								.content(
										"<![CDATA[\n components.reloadUrl=\""
												+ util.url(util.getReloadPath()) + "\" \n]]>",
						false)._head().body();
		super.render(component, html, util);
		html._body()._html();
	}
}
