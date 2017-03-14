package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValueHandle;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CTextField extends Component<CTextField> {

    private ValueHandle<String> value;

    static class Template extends BootstrapComponentTemplateBase<CTextField> {

        @Override
        public void doRender(CTextField component, BootstrapRiseCanvas<?> html) {
            if (component.isDisabled())
                html.input_text().BformControl().DISABLED().VALUE(component.getValue().get());
            else
                html.input_text().BformControl().Rvalue(component.getValue());
        }
    }

    public ValueHandle<String> getValue() {
        return value;
    }

    public CTextField() {

    }

    public CTextField( Supplier<String> value) {
        this.value = createValueHandle(value, true);
    }

    public CTextField value(ValueHandle<String> value) {
        this.value = value;
        return this;
    }

}
