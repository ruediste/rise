package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.component.tree.SingleChildRelation;

/**
 * Component containing a single child
 */
@DefaultTemplate(CComponentContainerTemplate.class)
public class CComponentContainer extends ComponentBase<CComponentContainer> {
    public final SingleChildRelation<Component, CComponentContainer> child = new SingleChildRelation<Component, CComponentContainer>(
            self());
}
