package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;

public class CTextField extends Component<CTextField> {

    private Supplier<String> value;

    static class Template extends ComponentTemplateBase<CTextField> {

        @Override
        public void doRender(CTextField component, RiseCanvas<?> html) {
            html.input_text().Rvalue(component.getValue());
        }
    }

    public Supplier<String> getValue() {
        return value;
    }

    public CTextField(@Capture Supplier<String> value) {
        this.value = value;
    }

}
