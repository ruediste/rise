package com.github.ruediste.rise.component.components;

import java.util.Optional;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class COptionalString extends COptionalInputBase<String> {

    static class Template extends COptionalInputBase.Template<String> {

        @Override
        Optional<String> extractValue(COptionalInputBase<String> component) {
            return util.getParameterValue(component, "value");
        }

        @Override
        protected void renderValueInput(COptionalInputBase<String> component, BootstrapRiseCanvas<?> html,
                String value) {
            html.input_text().NAME(util.getParameterKey(component, "value")).VALUE(value);
        }

    }

}
