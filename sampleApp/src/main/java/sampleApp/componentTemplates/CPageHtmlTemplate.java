package sampleApp.componentTemplates;

import static org.rendersnake.HtmlAttributesFactory.*;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.basic.CPage;
import laf.component.web.CWRenderUtil;
import laf.component.web.CWTemplateBase;
import laf.core.web.resource.*;

import org.rendersnake.HtmlCanvas;

public class CPageHtmlTemplate extends CWTemplateBase<CPage> {

	@Inject
	CWRenderUtil util;

	public static class Bundle extends StaticWebResourceBundle {

		private final ResourceOutput css = new ResourceOutput(this);

		private final ResourceOutput js = new ResourceOutput(this);

		private final ResourceOutput fonts = new ResourceOutput(this);

		@Override
		protected void initializeImpl() {

			ResourceGroup preMinified = paths("/static/js/jquery-1.11.1.js",
					"/static/bootstrap/js/bootstrap.js",
					"/static/bootstrap/css/bootstrap.css").insertMinInProd()
					.load(servletContext());
			ResourceGroup normal = paths("/static/css/sample-app.css").load(
					servletContext()).merge(
					paths("/js/componentWeb.js").load(classPath()).name(
							"/static{qname}.{ext}"));

			// provide fonts
			ResourceGroup fontGroup = paths(
					"/static/bootstrap/fonts/glyphicons-halflings-regular.eot",
					"/static/bootstrap/fonts/glyphicons-halflings-regular.woff",
					"/static/bootstrap/fonts/glyphicons-halflings-regular.tts",
					"/static/bootstrap/fonts/glyphicons-halflings-regular.svg")
					.load(servletContext());

			if (dev()) {
				preMinified.merge(normal).fork(x -> x.filter("js").send(js))
						.filter("css").send(css);
				fontGroup.send(fonts);
			}

			if (prod()) {
				preMinified
						.filter("css")
						.merge(normal.filter("css").process(
								processors.minifyCss()))
						.collect("/static/css/{hash}.css").send(css);

				preMinified
						.filter("js")
						.merge(normal.filter("js").process(
								processors.minifyJs()))
						.collect("/static/js/{hash}.js").send(js);
				fontGroup.name("/static/fonts/{name}.{ext}").send(fonts);
			}
		}

		public ResourceOutput getCss() {
			return css;
		}

		public ResourceOutput getJs() {
			return js;
		}

		public ResourceOutput getFonts() {
			return fonts;
		}
	}

	@Inject
	Bundle bundle;

	@Override
	public void render(CPage component, HtmlCanvas html) throws IOException {

		//@formatter:off
		html.write("<!DOCTYPE html>",false)
		.html()
			.head()
				.meta(name("viewport").content("width=device-width, initial-scale=1"))
				.title().content("Yeah")
				.render(util.cssBundle(bundle.getCss()))
			._head()
			.body(data("reloadurl", util.getReloadUrl()));

				super.render(component, html);
				html.render(util.jsBundle(bundle.getJs()))
			._body()
		._html();
	}
}
