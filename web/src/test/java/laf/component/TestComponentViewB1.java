package laf.component;

import laf.component.core.ComponentView;
import laf.component.core.ViewQualifier;
import laf.component.tree.Component;

@ViewQualifier(TestViewQualifier1.class)
public class TestComponentViewB1 extends ComponentView<TestControllerB> {

	@Override
	public Component createComponents() {
		return null;
	}

}
