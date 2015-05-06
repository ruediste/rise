package com.github.ruediste.laf.sample.component;

import com.github.ruediste.laf.api.CView;
import com.github.ruediste.laf.component.tree.Component;
import com.github.ruediste.laf.component.web.components.CPage;

public class SampleComponentView extends CView<SampleComponentController> {

	@Override
	protected Component createComponents() {
		return new CPage();
	}

}
