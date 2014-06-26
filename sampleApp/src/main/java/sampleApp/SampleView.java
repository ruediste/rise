package sampleApp;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import laf.mvc.html.MvcRenderUtil;
import laf.mvc.html.RendersnakeView;

import org.rendersnake.HtmlCanvas;

public class SampleView extends RendersnakeView<String> {

	@Override
	public void render(HtmlCanvas html, MvcRenderUtil util) throws IOException {
		util.startHtmlPage(html);
		html.head()
				.title()
				.content("Hello World")
				._head()
				.body()
				.div()
				.write(getData())
				._div()
				.a(href(util.url(util.path(SampleComponentController.class)
						.index()))).content("Component Controller")._body()
				._html();
	}

}
