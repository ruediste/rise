package laf.component;

import laf.component.core.ViewQualifier;
import laf.component.core.api.CView;
import laf.component.core.tree.Component;

@ViewQualifier(TestViewQualifier2.class)
public class TestComponentViewB2 extends CView<TestControllerB> {

	@Override
	public Component createComponents() {
		return null;
	}

}
