package laf.testApp.component;

import static org.rendersnake.HtmlAttributesFactory.id;

import javax.inject.Inject;

import laf.component.core.api.CView;
import laf.component.core.basic.*;
import laf.component.core.tree.Component;
import laf.component.web.components.*;
import laf.integration.IntegrationUtil;

public class TestComponentView extends CView<TestComponentController> {

	@Inject
	IntegrationUtil iu;

	@Override
	protected Component createComponents() {
		//@formatter:off
		return new CPage().add(new CGroup()
			.add(new CTextFieldFormGroup().tag("stringValue").bind(c->c.setText(controller.entity().getStringValue())))
			.add(new CButton("Trigger Page Reload").tag("reloadButton"))
			.add(new CButton("Save").tag("saveButton").handler(controller::save))
			.add(new CButton("Push Down").tag("pushDownButton").handler(controller::pushDown))
			.render(html->{html.span(id("checkerMessage")).content(controller.message().getMessage());})
			.render(html->{html.a(id("displayEntity").href(iu.mwUrl(iu.mwPath(EntityDisplayController.class).index(controller.entity())))).content("display entity");}));
	}
}
