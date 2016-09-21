package com.github.ruediste.rise.component.components;

import java.util.Optional;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class COptionalDouble extends COptionalInputBase<Double> {

    {
        defaultValue = 0.0;
    }

    static class Template extends COptionalInputBase.Template<Double> {

        @Override
        Optional<Double> extractValue(COptionalInputBase<Double> component) {
            return util.getParameterValue(component, "value").map(Double::parseDouble);
        }

        @Override
        protected void renderValueInput(COptionalInputBase<Double> component, BootstrapRiseCanvas<?> html,
                Double value) {
            html.input_number().NAME(util.getParameterKey(component, "value")).VALUE(value.toString());
        }

    }

}
