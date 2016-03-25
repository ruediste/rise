package com.github.ruediste.rise.component.components;

import java.time.LocalDate;
import java.util.Optional;

@DefaultTemplate(CLocalDateTemplate.class)
public class CLocalDate extends CInputBase<CLocalDate> {
    private Optional<LocalDate> value;

    public Optional<LocalDate> getValue() {
        return value;
    }

    public void setValue(Optional<LocalDate> value) {
        this.value = value;
    }
}
