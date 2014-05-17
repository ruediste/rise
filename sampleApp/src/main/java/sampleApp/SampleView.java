package sampleApp;

import java.io.IOException;

import laf.component.CRender;
import laf.component.ComponentView;

import org.rendersnake.HtmlCanvas;

public class SampleView extends ComponentView<SampleComponentController> {

	@Override
	public void createComponents() {
		children.add(new CRender() {

			@Override
			public void render(HtmlCanvas html) throws IOException {
				html.p().write(controller.getSampleText())._p();
			}
		});
	}
}
