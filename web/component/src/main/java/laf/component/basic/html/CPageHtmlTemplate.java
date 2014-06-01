package laf.component.basic.html;

import java.io.IOException;

import laf.component.basic.CPage;
import laf.component.html.PageReloadController;
import laf.component.html.RenderUtil;
import laf.component.html.template.HtmlTemplateBase;

import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;

public class CPageHtmlTemplate extends HtmlTemplateBase<CPage> {

	@Override
	public void render(CPage component, HtmlCanvas html, RenderUtil util)
			throws IOException {
		util.url(util.path(PageReloadController.class)
				.reloadPage(util.pageId()));

		// @formatter:off
		html.html()
				.head()
				.title()
				.content("Yeah")
				._head()
				.body()
				.form(HtmlAttributesFactory.method("post").action(
						util.url(util.path(PageReloadController.class)
								.reloadPage(util.pageId()))));
		super.render(component, html, util);
		html.input(HtmlAttributesFactory.type("submit"))._form()._body()
				._html();
	}
}
