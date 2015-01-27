package laf.testApp.component;

import static org.rendersnake.HtmlAttributesFactory.id;

import java.io.IOException;

import laf.testApp.dom.TestEntity;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.mvc.web.MWRenderUtil;
import com.github.ruediste.laf.mvc.web.MvcWebView;

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
