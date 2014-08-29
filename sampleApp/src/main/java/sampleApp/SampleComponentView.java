package sampleApp;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.ComponentViewUtil;
import laf.component.core.api.CView;
import laf.component.core.basic.*;
import laf.component.core.tree.Component;
import laf.component.web.api.CWRenderUtil;
import laf.component.web.basic.htmlTemplate.CRender;

import org.rendersnake.HtmlCanvas;

public class SampleComponentView extends
		CView<SampleComponentController> {

	@Inject
	ComponentViewUtil util;

	@Override
	public Component createComponents() {
		return new CPage()
				.add(new CRender() {

					@Override
					public void render(HtmlCanvas html, CWRenderUtil util)
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
