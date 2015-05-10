package com.github.ruediste.rise.sample.component;

import com.github.ruediste.rise.api.ViewComponent;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.web.components.CButton;
import com.github.ruediste.rise.component.web.components.CPage;
import com.github.ruediste.rise.component.web.components.CRender;
import com.github.ruediste.rise.component.web.components.CTextFieldFormGroup;

public class SampleComponentView extends ViewComponent<SampleComponentController> {

	@Override
	protected Component createComponents() {
		return new CPage()
				.add(new CRender(html -> html.write("Wird schon gut sein ..."
						+ controller.counter)))
				.add(new CButton("ClickMe").handler(() -> controller.inc()))
				.add(new CTextFieldFormGroup().bind(field -> field
						.setText(controller.getData().getText())));
	}
}
