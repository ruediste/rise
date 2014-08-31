package sampleApp;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import laf.mvc.web.MvcWebView;
import laf.mvc.web.api.MWRenderUtil;

import org.rendersnake.HtmlCanvas;

public class SampleView extends MvcWebView<String> {

	@Override
	public void render(HtmlCanvas html, MWRenderUtil util) throws IOException {
		// @formatter:off
		html.write("<!DOCTYPE html>",false)
		.html()
			.head()
				.title()
				.content("Hello World")
			._head()
			.body()
				.div()
				.write(getData())
				._div()
				.a(href(util.url(util.path(SampleComponentController.class)
						.index()))).content("Component Controller")
			._body()
		._html();

	}

}
