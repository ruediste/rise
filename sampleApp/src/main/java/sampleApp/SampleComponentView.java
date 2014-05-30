package sampleApp;

import java.io.IOException;

import laf.component.*;
import laf.component.basic.CRender;
import laf.component.basic.CTextField;
import laf.component.core.Component;
import laf.component.core.ComponentView;
import laf.component.html.RenderUtil;

import org.rendersnake.HtmlCanvas;

public class SampleComponentView extends
ComponentView<SampleComponentController> {

	@Override
	public Component createComponents() {
		return new SamplePage().body.add(new CRender() {

			@Override
			public void render(HtmlCanvas html, RenderUtil util)
					throws IOException {
				html.p().write(controller.getSampleText())._p();
			}
		}).body.add(new CTextField());
	}
}
