package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

public class CInputDouble extends CInputBase<Double, CInputDouble> {

    public CInputDouble() {
        super(InputType.number);
    }

    public CInputDouble( Supplier<Double> value) {
        this();
        value(value);
    }

    @Override
    public Double parse(String input) {
        return Double.valueOf(input);
    }

    @Override
    public String format(Double input) {
        return input.toString();
    }

}
