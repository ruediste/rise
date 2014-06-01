package laf.component.basic;

import laf.component.core.Component;
import laf.component.core.ComponentBase;
import laf.component.core.MultiChildrenRelation;

public class CPage extends ComponentBase<CPage> {
	public final MultiChildrenRelation<Component, CPage> body = new MultiChildrenRelation<>(
			this);
}
