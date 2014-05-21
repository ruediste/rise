package sampleApp;

import java.io.IOException;

import laf.component.*;

import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;

public class SamplePage extends ComponentBase<SamplePage> {

	public final MultiChildrenRelation<Component> body = new MultiChildrenRelation<>(
			this);

	public SamplePage(Component bodyComponent) {
		body.add(bodyComponent);
	}

	@Override
	public void render(HtmlCanvas html) throws IOException {
		// @formatter:off
		html.html().head().title().content("Yeah")._head().body()
				.form(HtmlAttributesFactory.method("post").action(null));
		super.render(html);
		html.input(HtmlAttributesFactory.type("submit"))._form()._body()
		._html();
	}
}
