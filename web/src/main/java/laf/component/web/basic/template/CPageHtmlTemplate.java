package laf.component.web.basic.template;

import static org.rendersnake.HtmlAttributesFactory.data;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.basic.CPage;
import laf.component.web.CWRenderUtil;
import laf.component.web.CWTemplateBase;

import org.rendersnake.HtmlCanvas;

public class CPageHtmlTemplate extends CWTemplateBase<CPage> {

	@Inject
	CWRenderUtil util;

	@Override
	public void render(CPage component, HtmlCanvas html) throws IOException {

		//@formatter:off
		html.write("<!DOCTYPE html>",false)
		.html()
			.head()
				.title().content("Yeah")
				.render(util.jsBundle("js/jquery-1.11.1.js", "js/components.js"))
				.render(util.cssBundle("css/components.css","css/test.sass"))
			._head()
			.body(data("reloadpath", util.url(util.getReloadPath()+"/"+util.getPageId())));
				super.render(component, html);
			html._body()
		._html();
	}
}
