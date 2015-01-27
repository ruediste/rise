package laf.testApp.smokeTest;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.mvc.web.MWRenderUtil;
import com.github.ruediste.laf.mvc.web.MvcWebView;

public class SmokeTestView extends MvcWebView<String> {

	@Override
	public void render(HtmlCanvas html, MWRenderUtil util) throws IOException {
		// @formatter:off
		html.write("<!DOCTYPE html>",false)
		.html()
			.head()
			._head()
			.body()
				.write(getData())
			._body()
		._html();
	}

}
