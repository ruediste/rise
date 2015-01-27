package sampleApp;

import static com.github.ruediste.laf.component.core.binding.transformers.Transformers.dateToString;
import static org.rendersnake.HtmlAttributesFactory.class_;

import javax.inject.Inject;

import com.github.ruediste.laf.component.core.api.CView;
import com.github.ruediste.laf.component.core.tree.Component;
import com.github.ruediste.laf.component.web.CWViewUtil;
import com.github.ruediste.laf.component.web.components.*;
import com.github.ruediste.laf.integration.IntegrationUtil;

public class SampleComponentView extends CView<SampleComponentController> {

	@Inject
	CWViewUtil util;

	@Inject
	IntegrationUtil integrationUtil;

	@Override
	public Component createComponents() {
		// @formatter:off
		return new CPage()
		.add(new CGroup()
			.render(html->html
					.div(class_("container"))
							.p().write(controller.getSampleText()).span(class_("glyphicon glyphicon-star"))._span()._p())
			.add(new CTextFieldFormGroup().setLabel("First Name").bind(c -> c.setText(controller.user().getFistName())))
			.add(new CTextFieldFormGroup().setLabel("Last Name").bind(c -> c.setText(controller.user().getLastName())))
			.add(new CTextFieldFormGroup().setLabel("Last Login Time").bind(c -> c.setText(dateToString(controller.user().getLastLogin()))))
			.add(new CButton("Reload").handler(()->controller.reload()))
			.add(new CButton("Save").handler(()->controller.save()))
			.add(new CLink("MVC Controller", () -> integrationUtil.mwUrl(integrationUtil.mwPath(SampleController.class).index())))
			.add(new CLink("Self", util.path(SampleComponentController.class).index()))
			.render(html->html._div())
		);
	}
}
