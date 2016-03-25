package com.github.ruediste.rise.component.components;

import java.time.LocalDate;
import java.util.Optional;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CLocalDateTemplate extends BootstrapComponentTemplateBase<CLocalDate> {

    @Override
    public void doRender(CLocalDate component, BootstrapRiseCanvas<?> html) {
        html.input().TYPE(InputType.text.toString()).BformControl().VALUE(component.getValue().toString())
                .rCOMPONENT_ATTRIBUTES(component).NAME(util.getKey(component, "value"));
    }

    @Override
    public void applyValues(CLocalDate component) {
        getParameterValue(component, "value")
                .map(val -> "".equals(val) ? Optional.<LocalDate> empty() : Optional.of(LocalDate.parse(val)))
                .ifPresent(component::setValue);
    }

}
