package laf.component.basic;

import laf.component.core.Component;
import laf.component.core.ComponentBase;
import laf.component.core.MultiChildrenRelation;

/**
 * Component representing a partial page reload context
 */
public class CReload extends ComponentBase<CReload> {

	public final MultiChildrenRelation<Component, CReload> children = new MultiChildrenRelation<>(
			this);

}
