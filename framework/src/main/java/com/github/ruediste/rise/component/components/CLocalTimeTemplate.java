package com.github.ruediste.rise.component.components;

import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CLocalTimeTemplate extends BootstrapComponentTemplateBase<CLocalTime> {

    @Override
    public void doRender(CLocalTime component, BootstrapRiseCanvas<?> html) {
        html.bInputGroup()

        .bInputGroupAddon().content("Hour").input().NAME(util.getKey(component, "hour"))
                .VALUE(component.getValue().map(t -> t.getHour()).map(Objects::toString).orElse(""))
                .TYPE(InputType.number.toString()).BformControl()

        .bInputGroupAddon().content("Minute").input().NAME(util.getKey(component, "minute"))
                .VALUE(component.getValue().map(t -> t.getMinute()).map(Objects::toString).orElse(""))
                .TYPE(InputType.number.toString()).BformControl()

        .bInputGroupAddon().content("Second").input().NAME(util.getKey(component, "second"))
                .VALUE(component.getValue().map(t -> t.getSecond()).map(Objects::toString).orElse(""))
                .TYPE(InputType.number.toString()).BformControl()

        ._bInputGroup();
        // html.input().TYPE(InputType.text.toString()).BformControl()
        // .VALUE(component.getValue().map(Objects::toString).orElse("")).rCOMPONENT_ATTRIBUTES(component)
        // .NAME(util.getKey(component, "value"));
    }

    @Override
    public void applyValues(CLocalTime component) {
        Optional<String> hour = getParameterValue(component, "hour");
        Optional<String> minute = getParameterValue(component, "minute");
        Optional<String> second = getParameterValue(component, "second");
        if (hour.isPresent() && minute.isPresent() && second.isPresent()) {
            if (hour.get().isEmpty() || minute.get().isEmpty() || second.get().isEmpty())
                component.setValue(Optional.empty());
            component.setValue(Optional.of(LocalTime.of(Integer.parseInt(hour.get()), Integer.parseInt(minute.get()),
                    Integer.parseInt(second.get()))));
        }
        // getParameterValue(component, "value")
        // .map(val -> "".equals(val) ? Optional.<LocalTime> empty() :
        // Optional.of(LocalTime.parse(val)))
        // .ifPresent(component::setValue);
    }
}
