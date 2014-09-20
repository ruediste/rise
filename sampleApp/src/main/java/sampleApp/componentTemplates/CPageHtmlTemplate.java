package sampleApp.componentTemplates;

import static org.rendersnake.HtmlAttributesFactory.*;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.basic.CPage;
import laf.component.web.CWRenderUtil;
import laf.component.web.CWTemplateBase;
import laf.core.web.resource.ResourceType;

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
				.meta(name("viewport").content("width=device-width, initial-scale=1"))
				.title().content("Yeah")
				.render(util.cssBundle("bootstrap/css/bootstrap.min.css","css/components.css"))
				// override the font-face definition from bootstrap
				.style().content(String.format("@font-face {\n" +
						"  font-family: 'Glyphicons Halflings';\n" +
						"\n" +
						"  src: url('%s');\n" +
						"  src: url('%s?#iefix') format('embedded-opentype'), \n" +
						"  url('%s') format('woff'), \n" +
						"  url('%s') format('truetype'), \n" +
						"  url('%s#glyphicons_halflingsregular') format('svg');\n" +
						"}",
						util.singleResource(ResourceType.valueOf("eot"), "bootstrap/fonts/glyphicons-halflings-regular.eot"),
						util.singleResource(ResourceType.valueOf("eot"), "bootstrap/fonts/glyphicons-halflings-regular.eot"),
						util.singleResource(ResourceType.valueOf("woff"), "bootstrap/fonts/glyphicons-halflings-regular.woff"),
						util.singleResource(ResourceType.valueOf("ttf"), "bootstrap/fonts/glyphicons-halflings-regular.ttf"),
						util.singleResource(ResourceType.valueOf("svg"), "bootstrap/fonts/glyphicons-halflings-regular.svg")
						),false)

			._head()
			.body(data("reloadurl", util.getReloadUrl()));

				super.render(component, html);
				html.render(util.jsBundle("js/jquery-1.11.1.js",
						"bootstrap/js/bootstrap.js",
						"js/componentWeb.js"))
			._body()
		._html();
	}
}
