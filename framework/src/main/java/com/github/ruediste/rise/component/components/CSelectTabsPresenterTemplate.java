package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CSelectTabsPresenterTemplate extends BootstrapComponentTemplateBase<CSelectTabsPresenter> {

    @Override
    public void doRender(CSelectTabsPresenter component, BootstrapRiseCanvas<?> html) {
        CSelectTabs<?> selectTabs = component.getSelectTabs();
        doRender(component, selectTabs, html);
    }

    private <T> void doRender(CSelectTabsPresenter component, CSelectTabs<T> selectTabs, BootstrapRiseCanvas<?> html) {
        html.render(selectTabs.childRelation().getChild(selectTabs.getSelectedItemOrElseFirst()).child().get());
    }

}
