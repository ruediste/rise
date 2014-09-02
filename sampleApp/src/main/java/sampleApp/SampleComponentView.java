package sampleApp;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.api.CView;
import laf.component.core.basic.*;
import laf.component.core.tree.Component;
import laf.component.web.CWRenderUtil;
import laf.component.web.CWViewUtil;
import laf.component.web.basic.CLink;
import laf.component.web.basic.template.CRender;

import org.rendersnake.HtmlCanvas;

public class SampleComponentView extends CView<SampleComponentController> {

	@Inject
	CWViewUtil util;

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
