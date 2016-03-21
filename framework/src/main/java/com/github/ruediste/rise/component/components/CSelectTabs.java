package com.github.ruediste.rise.component.components;

import java.util.Map;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.MultiChildrenRelation;

/**
 * Component showing a select input and displaying a different component for
 * each possible selection
 */
public class CSelectTabs<T> extends CSingleSelection<T, CSelectTabs<T>> {

    Map<T, Component> components

    public CSelectTabs<T> setComponent(T choice, Component component) {
        return this;
    }
}
