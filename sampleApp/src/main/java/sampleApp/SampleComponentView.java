package sampleApp;

import java.io.IOException;

import laf.component.basic.CButton;
import laf.component.basic.CPage;
import laf.component.basic.CTextField;
import laf.component.basic.html.CRender;
import laf.component.core.Component;
import laf.component.core.ComponentView;
import laf.component.html.RenderUtil;

import org.rendersnake.HtmlCanvas;

public class SampleComponentView extends
ComponentView<SampleComponentController> {

	@Override
	public Component createComponents() {
		return new CPage().add(new CRender() {

			@Override
			public void render(HtmlCanvas html, RenderUtil util)
					throws IOException {
				html.p().write(controller.getSampleText())._p();
			}
		}).add(new CTextField()).add(new CButton("Reload"));
	}
}
