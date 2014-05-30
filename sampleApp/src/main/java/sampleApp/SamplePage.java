package sampleApp;

import java.io.IOException;

import laf.component.*;
import laf.component.core.Component;
import laf.component.core.ComponentBase;
import laf.component.core.MultiChildrenRelation;
import laf.component.html.PageReloadController;
import laf.component.html.RenderUtil;

import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;

public class SamplePage extends ComponentBase<SamplePage> {

	public final MultiChildrenRelation<Component, SamplePage> body = new MultiChildrenRelation<>(
			this);

	public SamplePage() {
	}

	public SamplePage(Component bodyComponent) {
		body.add(bodyComponent);
	}

	@Override
	public void render(HtmlCanvas html, RenderUtil util) throws IOException {
		util.url(util.path(PageReloadController.class)
				.reloadPage(util.pageId()));

		// @formatter:off
		html.html()
		.head()
		.title()
		.content("Yeah")
		._head()
		.body()
		.form(HtmlAttributesFactory.method("post").action(
				util.url(util.path(PageReloadController.class)
						.reloadPage(util.pageId()))));
		super.render(html, util);
		html.input(HtmlAttributesFactory.type("submit"))._form()._body()
		._html();
	}
}
