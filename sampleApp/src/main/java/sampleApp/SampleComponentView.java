package sampleApp;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.basic.*;
import laf.component.basic.htmlTemplate.CRender;
import laf.component.core.ComponentView;
import laf.component.core.ComponentViewUtil;
import laf.component.html.RenderUtil;
import laf.component.tree.Component;

import org.rendersnake.HtmlCanvas;

public class SampleComponentView extends
		ComponentView<SampleComponentController> {

	@Inject
	ComponentViewUtil util;

	@Override
	public Component createComponents() {
		return new CPage()
				.add(new CRender() {

					@Override
					public void render(HtmlCanvas html, RenderUtil util)
							throws IOException {
						html.p().write(controller.getSampleText())._p();
					}
				})
				.add(new CGroup().add(new CTextField()).add(
						new CButton("Reload")))
				.add(new CLink("MVC controller", util.path(
						SampleController.class).index()));
	}
}
