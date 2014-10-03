package laf.skeleton.sample;

import static org.rendersnake.HtmlAttributesFactory.*;
import laf.component.core.basic.*;
import laf.component.core.tree.Component;
import laf.component.web.components.*;
import laf.skeleton.base.ComponentViewBase;

public class SampleComponentView extends
		ComponentViewBase<SampleComponentController> {

	@Override
	protected Component createComponents() {
		//@formatter:off
		return new CPage().add(new CGroup()
			.add(new CTextFieldFormGroup().tag("stringValue").bind(c->c.setText(controller.entity().getStringValue())))
			.add(new CButton("Trigger Page Reload").tag("reloadButton"))
			.add(new CButton("Save").tag("saveButton").handler(controller::save))
			.add(new CButton("Push Down").tag("pushDownButton").handler(controller::pushDown))
			.render(html->html.a(href(iUtil.mwUrl(iUtil.mwPath(SampleMvcController.class).index()))).content("Mvc Controller"))
			.render(html->{html.span(id("checkerMessage")).content(controller.message().getMessage());})
		);
	}
}
