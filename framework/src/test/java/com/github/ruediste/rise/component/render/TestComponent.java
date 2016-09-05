package com.github.ruediste.rise.component.render;

import java.util.Optional;

import com.github.ruediste.rise.component.components.ComponentTemplateBase;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

public class TestComponent extends Component<TestComponent> {

    private Runnable body;

    @ComponentState
    private String stateValueString;

    @ComponentState
    private Optional<String> stateValueOptional = Optional.empty();

    public static class Template extends ComponentTemplateBase<TestComponent> {

        @Override
        public void doRender(TestComponent component, RiseCanvas<?> html) {
            html.div().rCOMPONENT_ATTRIBUTES(component).render(component.body)._div();
        }
    }

    public TestComponent body(Runnable body) {
        this.body = body;
        return this;
    }

    public String getStateValueString() {
        return stateValueString;
    }

    public TestComponent setStateValueString(String stateValueString) {
        this.stateValueString = stateValueString;
        return this;
    }

    public Optional<String> getStateValueOptional() {
        return stateValueOptional;
    }

    public TestComponent setStateValueOptional(Optional<String> stateValueOptional) {
        this.stateValueOptional = stateValueOptional;
        return this;
    }
}
