package com.github.ruediste.rise.component.components;

import java.time.Duration;
import java.util.Optional;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class COptionalDuration extends COptionalInputBase<Duration> {

    static class Template extends COptionalInputBase.Template<Duration> {

        @Override
        protected void renderValueInput(COptionalInputBase<Duration> component, BootstrapRiseCanvas<?> html,
                Duration value) {
            html.input_number().NAME(util.getParameterKey(component, "value")).VALUE(value.toString()).write("s");
        }

        @Override
        Optional<Duration> extractValue(COptionalInputBase<Duration> component) {
            return util.getParameterValue(component, "value").map(x -> Duration.ofSeconds(Integer.parseInt(x)));
        }

    }

}
