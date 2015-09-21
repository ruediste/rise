package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.RelationsComponent;
import com.github.ruediste.rise.component.tree.SingleChildRelation;

/**
 * Component containing a single child
 */
@DefaultTemplate(RenderChildrenTemplate.class)
public class CComponentContainer
        extends RelationsComponent<CComponentContainer> {
    public final SingleChildRelation<Component, CComponentContainer> child = new SingleChildRelation<Component, CComponentContainer>(
            self());
}
