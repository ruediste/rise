package com.github.ruediste.rise.testApp.component;

import com.github.ruediste.rise.component.ViewQualifier;
import com.github.ruediste.rise.component.tree.Component;

@ViewQualifier(SampleViewQualifier.class)
public class SampleComponentViewAlternative extends SampleComponentViewBase {

	@Override
	protected Component createComponents() {
		return null;
	}

}
