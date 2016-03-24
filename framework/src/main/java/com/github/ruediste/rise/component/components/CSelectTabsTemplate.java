package com.github.ruediste.rise.component.components;

import java.util.Optional;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CSelectTabsTemplate<T> extends BootstrapComponentTemplateBase<CSelectTabs<T>> {

    @Override
    public void doRender(CSelectTabs<T> select, BootstrapRiseCanvas<?> html) {
        select.getSelectedItem().map(select.childRelation()::getChild)
                .orElseGet(() -> select.childRelation().getChildren().iterator().next());

        html.select().BformControl().CLASS("rise_c_selectTabs").rCOMPONENT_ATTRIBUTES(select)
                .NAME(util.getKey(select, "value"))
                .fIf(select.getSelectionHandler() != null, () -> html.CLASS("_selectionHandler"))
                .fForEach(select.getItemsAndChildren(), (idx, p) -> {
                    html.option().VALUE(String.valueOf(idx)).TEST_NAME(select.getTestName(p.getA()))
                            .fIf(select.isItemSelected(p.getA()), () -> html.SELECTED("selected"))
                            .render(p.getB().header().get())._option();
                })._select();
    }

    @Override
    public void applyValues(CSelectTabs<T> component) {
        getParameterValue(component, "value").ifPresent(idxStr -> {
            component.setSelectedItem(Optional.of(component.getItems().get(Integer.parseInt(idxStr))));
        });
    }

    @Override
    public void raiseEvents(CSelectTabs<T> component) {
        if (isParameterDefined(component, "changed") && component.getSelectionHandler() != null) {
            component.getSelectionHandler().accept(component.getSelectedItem());
        }
    }
}
