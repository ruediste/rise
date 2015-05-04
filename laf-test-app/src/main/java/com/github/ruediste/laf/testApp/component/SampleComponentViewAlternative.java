package com.github.ruediste.laf.testApp.component;

import com.github.ruediste.laf.component.ViewQualifier;
import com.github.ruediste.laf.component.tree.Component;

@ViewQualifier(SampleViewQualifier.class)
public class SampleComponentViewAlternative extends SampleComponentViewBase {

	@Override
	protected Component createComponents() {
		return null;
	}

}
