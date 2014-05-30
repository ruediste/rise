package laf.component;

import laf.component.core.Component;
import laf.component.core.ComponentView;
import laf.component.core.ViewQualifier;

@ViewQualifier(TestViewQualifier1.class)
public class TestComponentViewB1 extends ComponentView<TestControllerB> {

	@Override
	public Component createComponents() {
		return null;
	}

}
