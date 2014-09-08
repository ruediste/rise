package laf.component.web.basic.template;

import static org.rendersnake.HtmlAttributesFactory.*;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.basic.CPage;
import laf.component.web.CWRenderUtil;
import laf.component.web.CWTemplateBase;

import org.rendersnake.HtmlAttributesFactory;
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
				.script(src("/laf-sampleApp/static/js/jquery-1.11.1.js"))._script()
				.script(src("/laf-sampleApp//static/js/components.js"))._script()
				.link(rel("stylesheet").type("text/css").href("/laf-sampleApp/static/css/components.css"))
				.script(HtmlAttributesFactory.type("text/javascript")).content(
					"<![CDATA[\n components.reloadUrl=\""
					+ util.url(util.getReloadPath()) + "\" \n]]>",
					false)
			._head()
			.body(data("reloadpath", util.url(util.getReloadPath()+"/"+util.getPageId())));
				super.render(component, html);
			html._body()
		._html();
	}
}
