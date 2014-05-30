package laf.component;

import laf.component.core.Component;
import laf.component.core.ComponentView;
import laf.component.core.ViewQualifier;

@ViewQualifier(TestViewQualifier2.class)
public class TestComponentViewB2 extends ComponentView<TestControllerB> {

	@Override
	public Component createComponents() {
		return null;
	}

}
