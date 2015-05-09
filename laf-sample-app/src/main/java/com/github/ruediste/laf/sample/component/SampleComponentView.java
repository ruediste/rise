package com.github.ruediste.laf.sample.component;

import com.github.ruediste.laf.api.CView;
import com.github.ruediste.laf.component.tree.Component;
import com.github.ruediste.laf.component.web.components.CButton;
import com.github.ruediste.laf.component.web.components.CPage;
import com.github.ruediste.laf.component.web.components.CRender;
import com.github.ruediste.laf.component.web.components.CTextFieldFormGroup;

public class SampleComponentView extends CView<SampleComponentController> {

	@Override
	protected Component createComponents() {
		return new CPage()
				.add(new CRender(html -> html.write("Hello World "
						+ controller.counter)))
				.add(new CButton("ClickMe").handler(() -> controller.inc()))
				.add(new CTextFieldFormGroup().bind(field -> field
						.setText(controller.getData().getText())));
	}
}
