package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.google.common.base.Splitter;

public class CSortableTemplate
        extends BootstrapComponentTemplateBase<CSortable<?>> {

    @Override
    public void doRender(CSortable<?> component, BootstrapRiseCanvas<?> html) {
        doRenderImpl(component, html);
    }

    private <T> void doRenderImpl(CSortable<T> sortable,
            BootstrapRiseCanvas<?> html) {
        html.ul().CLASS("rise_sortable").DATA(
                CoreAssetBundle.componentAttributeNr,
                String.valueOf(util.getComponentNr(sortable)));
        int idx = 0;
        for (Component child : sortable.getChildren()) {
            html.li().DATA("rise-sortable-index", String.valueOf(idx))
                    .render(child)._li();
            idx++;
        }
        html._ul();
    }

    @Override
    public void applyValues(CSortable<?> component) {
        getParameterValue(component, "order").ifPresent(order -> {
            List<Integer> idxList = Splitter.on(',').splitToList(order).stream()
                    .map(Integer::parseInt).collect(toList());
            component.applyItemOrder(idxList);
        });
    }
}
