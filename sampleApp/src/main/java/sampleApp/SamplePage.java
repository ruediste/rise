package sampleApp;

import java.io.IOException;

import laf.component.Component;
import laf.component.ComponentBase;
import laf.component.MultiChildrenRelation;

import org.rendersnake.HtmlCanvas;

public class SamplePage extends ComponentBase<SamplePage> {

	public final MultiChildrenRelation<Component> body = new MultiChildrenRelation<>(
			this);

	public SamplePage(Component bodyComponent) {
		body.add(bodyComponent);
	}

	@Override
	public void render(HtmlCanvas html) throws IOException {
		html.html().head()._head().body();
		super.render(html);
		html._body()._html();
	}
}
