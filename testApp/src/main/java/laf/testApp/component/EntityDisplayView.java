package laf.testApp.component;

import static org.rendersnake.HtmlAttributesFactory.id;

import java.io.IOException;

import laf.mvc.web.MWRenderUtil;
import laf.mvc.web.MvcWebView;
import laf.testApp.dom.TestEntity;

import org.rendersnake.HtmlCanvas;

public class EntityDisplayView extends MvcWebView<TestEntity> {

	@Override
	public void render(HtmlCanvas html, MWRenderUtil util) throws IOException {
		// @formatter:off
		html.write("<!DOCTYPE html>",false)
		.html()
			.head()
			._head()
			.body()
				.write("stringValue: ")
				.span(id("stringValue"))
					.content(getData().getStringValue())
			._body()
		._html();
	}

}
