package com.github.ruediste.rise.component.components;

import java.util.Optional;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class COptionalInteger extends COptionalInputBase<Integer> {

    {
        defaultValue = 0;
    }

    static class Template extends COptionalInputBase.Template<Integer> {

        @Override
        protected void renderValueInput(COptionalInputBase<Integer> component, BootstrapRiseCanvas<?> html,
                Integer value) {
            html.input_number().BformControl().NAME(util.getParameterKey(component, "value"))
                    .VALUE(java.util.Objects.toString(value));
        }

        @Override
        Optional<Integer> extractValue(COptionalInputBase<Integer> component) {
            return util.getParameterValue(component, "value").map(Integer::parseInt);
        }

    }

}
