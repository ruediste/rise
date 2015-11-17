package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Consumer;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.google.common.base.Splitter;

public class CSortableTemplate
        extends BootstrapComponentTemplateBase<CSortable<?>> {

    private static AttachedProperty<CSortable<?>, Boolean> renderDiv = new AttachedProperty<>(
            "renderDiv");

    public static Consumer<CSortable> renderDiv() {
        return sortable -> renderDiv.set(sortable, true);
    }

    @Override
    public void doRender(CSortable<?> component, BootstrapRiseCanvas<?> html) {
        doRenderImpl(component, html);
    }

    private <T> void doRenderImpl(CSortable<T> sortable,
            BootstrapRiseCanvas<?> html) {
        boolean useDiv = Boolean.TRUE.equals(renderDiv.get(sortable));
        //@formatter:off
        html.fIf(useDiv, () -> html.div(), () -> html.ul())
                .CLASS("rise_sortable").CLASS(sortable.CLASS())
                .DATA(CoreAssetBundle.componentAttributeNr,
                        String.valueOf(util.getComponentNr(sortable)))
                .TEST_NAME(sortable.TEST_NAME())
          .renderChildren(sortable)
        .close();
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
