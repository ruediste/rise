package com.github.ruediste.rise.component.components;

import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CLocalTimeTemplate extends BootstrapComponentTemplateBase<CLocalTime> {

    @Override
    public void doRender(CLocalTime component, BootstrapRiseCanvas<?> html) {
        html.input().TYPE(InputType.text.toString()).BformControl()
                .VALUE(component.getValue().map(Objects::toString).orElse("")).rCOMPONENT_ATTRIBUTES(component)
                .NAME(util.getKey(component, "value"));
    }

    @Override
    public void applyValues(CLocalTime component) {
        getParameterValue(component, "value")
                .map(val -> "".equals(val) ? Optional.<LocalTime> empty() : Optional.of(LocalTime.parse(val)))
                .ifPresent(component::setValue);
    }
}
