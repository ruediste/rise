package sampleApp;

import java.io.IOException;

import laf.component.*;

import org.rendersnake.HtmlCanvas;

public class SamplePage extends ComponentView<SampleComponentController> {

	public final MultiChildrenRelation<Component> body = new MultiChildrenRelation<>(
			this);

	public SamplePage(SampleView sampleView) {
		body.add(sampleView);
	}

	@Override
	public void render(HtmlCanvas html) throws IOException {
		html.html().head()._head().body();
		super.render(html);
		html._body()._html();
	}
}
