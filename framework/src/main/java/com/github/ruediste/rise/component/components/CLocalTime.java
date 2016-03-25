package com.github.ruediste.rise.component.components;

import java.time.LocalTime;
import java.util.Optional;

@DefaultTemplate(CLocalTimeTemplate.class)
public class CLocalTime extends CInputBase<CLocalTime> {
    private Optional<LocalTime> value = Optional.empty();

    public Optional<LocalTime> getValue() {
        return value;
    }

    public void setValue(Optional<LocalTime> value) {
        this.value = value;
    }
}
