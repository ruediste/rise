package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

public class CInputLong extends CInputBase<Long, CInputLong> {

    public CInputLong() {
        super(InputType.number);
    }

    public CInputLong(Supplier<Long> value) {
        this();
        value(value);
    }

    @Override
    public Long parse(String input) {
        return Long.valueOf(input);
    }

    @Override
    public String format(Long input) {
        return input.toString();
    }

}
