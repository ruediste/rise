package com.github.ruediste.rise.component.components;

/**
 * Component showing a {@code <select>} element
 */
@DefaultTemplate(CSelectTemplate.class)
public class CSelect<T> extends CSingleSelection<T, CSelect<T>> {

    private boolean allowEmpty;

    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public CSelect<T> setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
        return this;
    }

}
