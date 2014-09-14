package sampleApp;

import static laf.component.core.binding.transformers.Transformers.dateToString;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.api.CView;
import laf.component.core.basic.*;
import laf.component.core.tree.Component;
import laf.component.web.CWRenderUtil;
import laf.component.web.CWViewUtil;
import laf.component.web.basic.CLink;
import laf.component.web.basic.template.CRender;
import laf.integration.IntegrationUtil;

import org.rendersnake.HtmlCanvas;

public class SampleComponentView extends CView<SampleComponentController> {

	@Inject
	CWViewUtil util;

	@Inject
	IntegrationUtil integrationUtil;

	@Override
	public Component createComponents() {
		// @formatter:off
		Component result=new CPage()
		.add(new CRender() {

			@Override
			public void render(HtmlCanvas html, CWRenderUtil util)
					throws IOException {
				html.p().write(controller.getSampleText())._p();
			}
		})
		.add(new CGroup()
				.add(new CTextField().bind(c -> c.setValue(controller.user().getFistName())))
				.add(new CTextField().bind(c -> c.setValue(controller.user().getLastName())))
				.add(new CTextField().bind(c -> c.setValue(dateToString(controller.user().getLastLogin()))))
				.add(new CButton("Reload").withHandler(()->controller.reload()))
				.add(new CButton("Save").withHandler(()->controller.save()))
				)
		.add(new CLink("MVC Controller", () -> integrationUtil.mwUrl(integrationUtil.mwPath(SampleController.class).index())))
		.add(new CLink("Self", util.path(SampleComponentController.class).index()));
		controller.hack();
		return result;
	}
}
