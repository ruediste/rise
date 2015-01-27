package com.github.ruediste.laf.component.core;

import com.github.ruediste.laf.component.core.ViewQualifier;
import com.github.ruediste.laf.component.core.api.CView;
import com.github.ruediste.laf.component.core.tree.Component;

@ViewQualifier(TestViewQualifier1.class)
public class TestComponentViewB1 extends CView<TestControllerB> {

	@Override
	public Component createComponents() {
		return null;
	}

}
