package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.List;

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
        //@formatter:off
        html.ul()
                .CLASS("rise_sortable").CLASS(sortable.CLASS())
                .rCOMPONENT_ATTRIBUTES(sortable)
                .fForEach(sortable.getItemsAndChildren(), p->
                    html.li().fIfPresent(sortable.getTestName(p.getA()), html::TEST_NAME)
                        .render(p.getB())
                    ._li())
        ._ul(); 
        //@formatter:on
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
