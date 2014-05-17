package sampleApp;

import java.io.IOException;

import laf.component.CRender;
import laf.component.Component;
import laf.component.ComponentView;

import org.rendersnake.HtmlCanvas;

public class SampleComponentView extends
		ComponentView<SampleComponentController> {

	@Override
	public Component createComponents() {
		return new SamplePage(new CRender() {

			@Override
			public void render(HtmlCanvas html) throws IOException {
				html.p().write(controller.getSampleText())._p();
			}
		});
	}
}
