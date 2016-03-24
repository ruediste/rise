package com.github.ruediste.rise.component.tree;

/**
 * Base class for components showing headers and
 */
public class CHeaderComponentBase<TSelf extends CHeaderComponentBase<TSelf>> extends RelationsComponent<TSelf> {
    private final SingleChildRelation<Component, TSelf> header = new SingleChildRelation<Component, TSelf>(self());
    private final SingleChildRelation<Component, TSelf> child = new SingleChildRelation<Component, TSelf>(self());

    public SingleChildRelation<Component, TSelf> header() {
        return header;
    }

    public SingleChildRelation<Component, TSelf> child() {
        return child;
    }
}
