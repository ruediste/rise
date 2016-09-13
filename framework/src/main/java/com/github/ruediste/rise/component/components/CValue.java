package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValueHandle;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;

public class CValue extends Component<CValue> {

    private ValueHandle<String> value;

    static class Template extends ComponentTemplateBase<CValue> {

        @Override
        public void doRender(CValue component, RiseCanvas<?> html) {
            if (component.getValue() == null)
                throw new RuntimeException("value not set");

            html.addAttribute("value", component.getValue().get()).NAME(util.getParameterKey(component, "value"));

        }

        @Override
        public void applyValues(CValue component) {
            util.getParameterValue(component, "value").ifPresent(component.getValue()::set);
        }

    }

    public CValue(String value) {
        value(value);
    }

    public CValue(@Capture Supplier<String> value) {
        value(value);
    }

    public CValue(@Capture Supplier<String> value, boolean isLabelProperty) {
        value(value, isLabelProperty);
    }

    public ValueHandle<String> getValue() {
        return value;
    }

    public CValue value(String value) {
        this.value = createValueHandle(value, true);
        return this;
    }

    public CValue value(@Capture Supplier<String> value) {
        return value(value, true);
    }

    public CValue value(@Capture Supplier<String> value, boolean isLabelProperty) {
        this.value = createValueHandle(value, isLabelProperty);
        return this;
    }
}
