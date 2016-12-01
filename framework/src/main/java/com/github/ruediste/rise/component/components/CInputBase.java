package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValueHandle;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;

public abstract class CInputBase<T, TSelf extends CInputBase<?, TSelf>> extends Component<TSelf> {
    ValueHandle<T> value;
    public final InputType type;

    public static class Template<T, TSelf extends CInputBase<T, TSelf>> extends BootstrapComponentTemplateBase<TSelf> {

        @Override
        public void doRender(TSelf component, BootstrapRiseCanvas<?> html) {
            html.input().TYPE(component.type.toString()).BformControl().NAME(util.getParameterKey(component, "value"))
                    .VALUE(component.format(component.value.get()));
        }

        @Override
        public void applyValues(TSelf component) {
            util.getParameterValue(component, "value").map(component::parse).ifPresent(component.value::set);
        }

    }

    public CInputBase(InputType type) {
        this.type = type;
    }

    public ValueHandle<T> getValue() {
        return value;
    }

    public TSelf value(@Capture Supplier<T> value) {
        return value(createValueHandle(value, true));
    }

    public TSelf value(ValueHandle<T> value) {
        this.value = value;
        return self();
    }

    public abstract T parse(String input);

    public abstract String format(T input);
}
