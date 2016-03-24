package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.RelationsComponent;

/**
 * Presents the components of a {@link CSelectTabs}
 */
@DefaultTemplate(CSelectTabsPresenterTemplate.class)
public class CSelectTabsPresenter extends RelationsComponent<CSelectTabsPresenter> {

    private CSelectTabs<?> selectTabs;

    public CSelectTabsPresenter() {
    }

    public CSelectTabsPresenter(CSelectTabs<?> selectTabs) {
        this.selectTabs = selectTabs;
    }

    public CSelectTabsPresenter setSelectTabs(CSelectTabs<?> selectTabs) {
        this.selectTabs = selectTabs;
        return this;
    }

    public CSelectTabs<?> getSelectTabs() {
        return selectTabs;
    }

}
