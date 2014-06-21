package laf.component.basic;

import laf.component.tree.Component;
import laf.component.tree.ComponentBase;
import laf.component.tree.MultiChildrenRelation;

/**
 * Component representing a partial page reload context
 */
public class CReload extends ComponentBase<CReload> {

	public final MultiChildrenRelation<Component, CReload> children = new MultiChildrenRelation<>(
			this);

}
