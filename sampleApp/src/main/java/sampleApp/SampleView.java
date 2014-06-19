package sampleApp;

import java.io.IOException;

import laf.mvc.html.MvcRenderUtil;
import laf.mvc.html.RendersnakeView;

import org.rendersnake.HtmlCanvas;

public class SampleView extends RendersnakeView<String> {

	@Override
	public void render(HtmlCanvas canvas, MvcRenderUtil util)
			throws IOException {
		canvas.html().head().title().content("Hello World")._head().body()
		.write(getData())._body()._html();
	}

}
