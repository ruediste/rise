package laf.testApp.component;

import static org.rendersnake.HtmlAttributesFactory.id;

import javax.inject.Inject;

import com.github.ruediste.laf.component.core.api.CView;
import com.github.ruediste.laf.component.core.tree.Component;
import com.github.ruediste.laf.component.web.components.*;
import com.github.ruediste.laf.integration.IntegrationUtil;

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
