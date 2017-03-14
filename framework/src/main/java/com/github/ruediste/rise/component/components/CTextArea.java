package com.github.ruediste.rise.component.components;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValueHandle;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CTextArea extends Component<CTextArea> {

    private ValueHandle<String> value;

    private Consumer<BootstrapRiseCanvas<?>> customizer;

    static class Template extends BootstrapComponentTemplateBase<CTextArea> {

        @Override
        public void doRender(CTextArea component, BootstrapRiseCanvas<?> html) {
            String value = component.value.get();
            if (component.isDisabled()) {
                html.pre().BformControl().DISABLED()
                        .fIf(component.customizer != null, () -> component.customizer.accept(html))
                        .content(value == null ? "" : value);
            } else
                html.textarea().BformControl().NAME(util.getParameterKey(component, "value"))
                        .fIf(component.customizer != null, () -> component.customizer.accept(html))
                        .content(value == null ? "" : value);
        }

        @Override
        public void applyValues(CTextArea component) {
            if (!component.isDisabled())
                util.getParameterValue(component, "value").ifPresent(component.value::set);
        }
    }

    public ValueHandle<String> getValue() {
        return value;
    }

    public CTextArea value(ValueHandle<String> value) {
        this.value = value;
        return this;
    }

    public CTextArea() {

    }

    public CTextArea( Supplier<String> value) {
        this.value = createValueHandle(value, true);
    }

    public Consumer<BootstrapRiseCanvas<?>> getCustomizer() {
        return customizer;
    }

    public CTextArea customizer(Runnable customizer) {
        return customizer(html -> customizer.run());
    }

    public CTextArea customizer(Consumer<BootstrapRiseCanvas<?>> customizer) {
        this.customizer = customizer;
        return this;
    }
}
