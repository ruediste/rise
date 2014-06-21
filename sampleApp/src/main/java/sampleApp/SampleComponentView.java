package sampleApp;

import java.io.IOException;

import laf.component.basic.*;
import laf.component.basic.htmlTemplate.CRender;
import laf.component.core.ComponentView;
import laf.component.html.RenderUtil;
import laf.component.tree.Component;

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
