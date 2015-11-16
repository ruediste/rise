package com.github.ruediste.rise.component.tree;

import java.util.ArrayList;

import com.google.common.collect.Iterables;

/**
 * Base class for the {@link Component} interface. Implements the parent-child
 * relation using {@link ChildRelation}s.
 *
 * <p>
 * <img src="doc-files/childRelation.png" alt="class hierarchy overview">
 *
 * @param <TSelf>
 *            type of this component
 */
public class RelationsComponent<TSelf extends RelationsComponent<TSelf>>
        extends ComponentBase<TSelf> {

    ArrayList<ChildRelation<?>> childRelations = new ArrayList<>();

    @Override
    public Iterable<Component> getChildren() {
        return Iterables.concat(childRelations);
    }

    @Override
    public void childRemoved(Component child) {
        for (ChildRelation<?> relation : childRelations) {
            relation.childRemoved(child);
        }
    }

    void addChildRelation(ChildRelation<?> childRelation) {
        childRelations.add(childRelation);
    }
}
