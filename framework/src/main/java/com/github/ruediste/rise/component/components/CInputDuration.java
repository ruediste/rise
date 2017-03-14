package com.github.ruediste.rise.component.components;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CInputDuration extends CInputBase<Duration, CInputDuration> {

    public static class Template extends CInputBase.Template<Duration, CInputDuration> {
        @Override
        public void doRender(CInputDuration component, BootstrapRiseCanvas<?> html) {
            html.bInputGroup();
            super.doRender(component, html);
            html.bInputGroupAddon().content("s")._bInputGroup();
        }
    }

    public CInputDuration() {
        super(InputType.number);
    }

    public CInputDuration(Supplier<Duration> value) {
        this();
        value(value);
    }

    @Override
    public Duration parse(String input) {
        return Duration.ofMillis((long) (1000 * Double.valueOf(input)));
    }

    @Override
    public String format(Duration input) {
        return Objects.toString(input.toMillis() / 1000.0);
    }

}
