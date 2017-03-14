package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValueHandle;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CCheckBox extends Component<CCheckBox> {

    @Inject
    ComponentUtil util;

    private ValueHandle<Boolean> value;

    public CCheckBox( Supplier<Boolean> value) {
        this.value = createValueHandle(value, true);
    }

    public static class Template extends ComponentTemplateBase<CCheckBox> {

        @Override
        public void doRender(CCheckBox component, RiseCanvas<?> html) {
            html.input_checkbox().rCOMPONENT_ATTRIBUTES(component).NAME(util.getParameterKey(component, "value"))
                    .VALUE("true");
            if (component.value.get())
                html.CHECKED();
        }

        @Override
        public void applyValues(CCheckBox component) {
            component.value.set(util.getParameterValue(component, "value").isPresent());
        }
    }

    public Supplier<Boolean> getValue() {
        return value;
    }

    public void setValue( Supplier<Boolean> value) {
        this.value = createValueHandle(value, true);
    }

}
