package sampleApp;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import javax.inject.Inject;

import laf.integration.IntegrationUtil;
import laf.mvc.web.MWRenderUtil;
import laf.mvc.web.MvcWebView;

import org.rendersnake.HtmlCanvas;

public class SampleView extends MvcWebView<String> {

	@Inject
	IntegrationUtil integrationUtil;

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
					.a(href(integrationUtil.cwUrl(integrationUtil.cwPath(SampleComponentController.class)
						.index()))).content("Component Controller")
				.a(href(util.url(util.path(SampleController.class)
						.index()))).content("Self")
			._body()
		._html();

	}

}
