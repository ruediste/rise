package com.github.ruediste.rise.testApp.component;

import com.github.ruediste.rise.component.ViewQualifier;
import com.github.ruediste.rise.component.tree.Component;

@ViewQualifier(TestViewQualifier.class)
public class TestComponentViewAlternative extends TestComponentViewBase {

	@Override
	protected Component createComponents() {
		return null;
	}

}
