package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import com.github.ruediste.rise.nonReloadable.lambda.Capture;

public class CInputDouble extends CInputBase<Double, CInputDouble> {

    public CInputDouble() {
        super(InputType.number);
    }

    public CInputDouble(@Capture Supplier<Double> value) {
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
