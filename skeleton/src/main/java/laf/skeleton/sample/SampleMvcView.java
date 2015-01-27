package laf.skeleton.sample;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import laf.skeleton.base.MvcViewBase;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.mvc.web.MWRenderUtil;

public class SampleMvcView extends MvcViewBase<String> {

	@Override
	public void render(HtmlCanvas html, MWRenderUtil util) throws IOException {
		// @formatter:off
		html.write("<!DOCTYPE html>",false)
		.html()
			.head()
			._head()
			.body()
				.div()
					.write("Hello World")
				._div()
				.a(href(iUtil.cwUrl(iUtil.cwPath(SampleComponentController.class).index())))
					.content("Sample Component View")
			._body()
		._html();
	}

}
