package sampleApp;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.integration.IntegrationUtil;
import com.github.ruediste.laf.mvc.web.MWRenderUtil;
import com.github.ruediste.laf.mvc.web.MvcWebView;

import sampleApp.entities.User;

public class SampleView extends MvcWebView<User> {

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
				.write(getData().getFistName()+" "+getData().getLastName())
				._div()
					.a(href(integrationUtil.cwUrl(integrationUtil.cwPath(SampleComponentController.class)
						.index()))).content("Component Controller")
				.a(href(util.url(util.path(SampleController.class)
						.index()))).content("Self")
			._body()
		._html();

	}

}
