package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CCheckBoxTemplate extends BootstrapComponentTemplateBase<CCheckBox> {

    @Inject
    ComponentUtil util;

    @Override
    public void applyValues(CCheckBox component) {
        component.setChecked(getParameterValue(component, "value").map(x -> "true".equals(x)).orElse(false));
    }

    @Override
    public void raiseEvents(CCheckBox component) {
        if (isParameterDefined(component, "changed"))
            component.getToggledHandler().ifPresent(h -> h.accept(component.isChecked()));
    }

    @Override
    public void doRender(CCheckBox component, BootstrapRiseCanvas<?> html) {
        boolean hasLabel = !component.label.getChildren().isEmpty();
        html.fIf(hasLabel, () -> html.label());

        html.input().TYPE(InputType.checkbox.toString())
                .fIf(!(component.getParent() instanceof CInputGroupAddon), () -> html.BformControl())
                .CLASS("rise_c_checkbox")
                .fIf(component.getToggledHandler().isPresent(), () -> html.CLASS("_handlerPresent"))
                .fIf(component.isChecked(), () -> html.CHECKED()).VALUE("true").NAME(util.getKey(component, "value"))
                .rCOMPONENT_ATTRIBUTES(component).fIf(component.isInline(), () -> html.BcheckboxInline());
        html.fIf(hasLabel, () -> html.renderChildren(component)._label());
    }
}
