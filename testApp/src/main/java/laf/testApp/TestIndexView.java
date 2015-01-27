package laf.testApp;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import javax.inject.Inject;

import laf.testApp.component.TestComponentController;
import laf.testApp.smokeTest.SmokeTestController;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.integration.IntegrationUtil;
import com.github.ruediste.laf.mvc.web.MWRenderUtil;
import com.github.ruediste.laf.mvc.web.MvcWebView;

public class TestIndexView extends MvcWebView<String> {

	@Inject
	IntegrationUtil iUtil;

	@Inject
	MWRenderUtil util;

	@Override
	public void render(HtmlCanvas html, MWRenderUtil util) throws IOException {
		// @formatter:off
		html.write("<!DOCTYPE html>",false)
		.html()
			.head()
			._head()
			.body()
				.a(href(util.url(util.path(SmokeTestController.class).index())))
					.content("SmokeTest")
				.a(href(iUtil.cwUrl(iUtil.cwPath(TestComponentController.class).index())))
					.content("TestComponent")
			._body()
		._html();
	}

}
