package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;

/**
 * Component showing a {@code <select>} element
 */
@DefaultTemplate(CSelectTemplate.class)
public class CSelect<T> extends CSingleSelection<T, Component, CSelect<T>> {

    private boolean allowEmpty;

    public CSelect() {
        setChildComponentFactory(i -> new CText(String.valueOf(i)));
    }

    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public CSelect<T> setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
        return this;
    }

}
