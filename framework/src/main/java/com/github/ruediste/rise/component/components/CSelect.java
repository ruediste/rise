package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;
import com.github.ruediste1.i18n.label.LabelUtil;

/**
 * Component showing a {@code <select>} element
 */
@DefaultTemplate(CSelectTemplate.class)
public class CSelect<T> extends CSingleSelection<T, Component, CSelect<T>> {

    private boolean allowEmpty;

    public CSelect() {
        LabelUtil labelUtil = InjectorsHolder.getInstance(LabelUtil.class);
        setChildComponentFactory(i -> new CText(labelUtil.getLabel(i)));
    }

    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public CSelect<T> setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
        return this;
    }

}
