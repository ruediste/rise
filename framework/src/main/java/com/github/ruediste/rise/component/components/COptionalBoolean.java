package com.github.ruediste.rise.component.components;

import java.util.Optional;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class COptionalBoolean extends COptionalInputBase<Boolean> {

    {
        defaultValue = false;
    }

    static class Template extends COptionalInputBase.Template<Boolean> {

        @Override
        Optional<Boolean> extractValue(COptionalInputBase<Boolean> component) {
            return Optional.of(util.getParameterValue(component, "value").isPresent());
        }

        @Override
        protected void renderValueInput(COptionalInputBase<Boolean> component, BootstrapRiseCanvas<?> html,
                Boolean value) {
            html.input_checkbox().NAME(util.getParameterKey(component, "value")).VALUE("true").fIf(value,
                    () -> html.CHECKED());
        }

    }

}
