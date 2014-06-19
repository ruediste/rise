package laf.component.basic.html;

import static org.rendersnake.HtmlAttributesFactory.*;

import java.io.IOException;

import laf.component.basic.CPage;
import laf.component.html.PageReloadController;
import laf.component.html.RenderUtil;
import laf.component.html.template.HtmlTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CPageHtmlTemplate extends HtmlTemplateBase<CPage> {

	@Override
	public void render(CPage component, HtmlCanvas html, RenderUtil util)
			throws IOException {

		// @formatter:off
		html.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">", false);
		html.html(xmlns("http://www.w3.org/1999/xhtml"))
		.head()
		.title()
		.content("Yeah")
		.script(src(util.resourceUrl("js/jquery-1.11.1.js")))._script()
		.script(src(util.resourceUrl("js/components.js")))._script()
		.link(rel("stylesheet").type("text/css").href(util.resourceUrl("css/components.css")))
		.script(type("text/javascript"))
		.content("<![CDATA[\n components.reloadUrl=\""+util.url(util.path(PageReloadController.class)
				.reloadPage(util.pageId()))+"\" \n]]>", false)
				._head()
				.body()
				;
		super.render(component, html, util);
		html._body()
		._html();
	}
}
