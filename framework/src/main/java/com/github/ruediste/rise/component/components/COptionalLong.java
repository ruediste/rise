package com.github.ruediste.rise.component.components;

import java.util.Optional;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class COptionalLong extends COptionalInputBase<Long> {

    static class Template extends COptionalInputBase.Template<Long> {

        @Override
        Optional<Long> extractValue(COptionalInputBase<Long> component) {
            return util.getParameterValue(component, "value").map(Long::parseLong);
        }

        @Override
        protected void renderValueInput(COptionalInputBase<Long> component, BootstrapRiseCanvas<?> html, Long value) {
            html.input_number().NAME(util.getParameterKey(component, "value")).VALUE(value.toString());
        }

    }

}
