package com.github.ruediste.rise.component.components;

import java.util.Objects;

import com.github.ruediste.rise.component.tree.CHeaderComponent;

/**
 * Component showing a select input and displaying a different component for
 * each possible selection
 */
@DefaultTemplate(CSelectTabsTemplate.class)
public class CSelectTabs<T> extends CSingleSelection<T, CHeaderComponent, CSelectTabs<T>> {

    public CSelectTabs() {
        setChildComponentFactory(i -> new CHeaderComponent().header().set(new CText(Objects.toString(i))).child()
                .set(new CText(Objects.toString(i))));
    }

    public CSelectTabsPresenter createPresenter() {
        return new CSelectTabsPresenter(this);
    }
}
