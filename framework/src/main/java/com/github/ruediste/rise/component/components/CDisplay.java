package com.github.ruediste.rise.component.components;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValueHandle;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;

/**
 * Component controlling if a component is displayed. Can be toogled on the
 * client side.
 */
public class CDisplay extends Component<CDisplay> {

    private ValueHandle<Boolean> isDisplayed;

    static class Template extends BootstrapComponentTemplateBase<CDisplay> {

        @Override
        public void doRender(CDisplay component, BootstrapRiseCanvas<?> html) {
            html.DATA("rise-cdisplay-nr", Objects.toString(util.getComponentNr(component)));
            html.DATA("rise-cdisplay-key", util.getParameterKey(component, "displayed"));
            html.DATA("rise-cdisplay-displayed", component.isDisplayed.get() ? "true" : "false");
        }

        @Override
        public void applyValues(CDisplay component) {
            Optional<Object> value = util.getParameterObject(component, "displayed");
            if (value.isPresent()) {
                component.isDisplayed.set((boolean) value.get());
            }
        }
    }

    public CDisplay() {
    }

    public CDisplay(boolean isDisplayed) {
        this.isDisplayed = createValueHandle(isDisplayed, false);
    }

    public CDisplay(@Capture Supplier<Boolean> isDisplayed) {
        this.isDisplayed = createValueHandle(isDisplayed, false);
    }

}
