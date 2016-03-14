package com.github.ruediste.rise.component.components;

import java.util.Optional;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CSelectTemplate extends BootstrapComponentTemplateBase<CSelect<?>> {

    @Override
    public void doRender(CSelect<?> select, BootstrapRiseCanvas<?> html) {
        doRenderImpl(select, html);
    }

    public <T> void doRenderImpl(CSelect<T> select, BootstrapRiseCanvas<?> html) {
        if (!select.isAllowEmpty() && !select.getSelectedItem().isPresent())
            throw new RuntimeException("CSelect does not allow an empty selection, but no item is selected");
        html.select().BformControl().CLASS("rise_c_select").rCOMPONENT_ATTRIBUTES(select)
                .NAME(util.getKey(select, "value"))
                .fIf(select.getSelectionHandler() != null, () -> html.CLASS("_selectionHandler"))
                .fIf(select.isAllowEmpty(),
                        () -> html.option().VALUE("-").TEST_NAME("-")
                                .fIf(!select.getSelectedItem().isPresent(), () -> html.SELECTED("selected"))._option())
                .fForEach(select.getItemsAndChildren(), (idx, p) -> {
                    html.option().VALUE(String.valueOf(idx)).TEST_NAME(select.getTestName(p.getA()))
                            .fIf(select.isItemSelected(p.getA()), () -> html.SELECTED("selected")).render(p.getB())
                            ._option();
                })._select();
    }

    @Override
    public void applyValues(CSelect<?> component) {
        applyValuesImpl(component);
    }

    public <T> void applyValuesImpl(CSelect<T> component) {
        getParameterValue(component, "value").ifPresent(idxStr -> {
            if (component.isAllowEmpty() && "-".equals(idxStr))
                component.setSelectedItem(Optional.empty());
            else
                component.setSelectedItem(Optional.of(component.getItems().get(Integer.parseInt(idxStr))));
        });
    }

    @Override
    public void raiseEvents(CSelect<?> component) {
        raiseEventsImpl(component);
    }

    public <T> void raiseEventsImpl(CSelect<T> select) {
    }
}
