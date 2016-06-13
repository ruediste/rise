package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;

public class CTextField extends ComponentBase<CTextField> {

    private Supplier<String> value;

    public CTextField(@Capture Supplier<String> value) {
        this.value = value;
    }

    @Override
    public void render(RiseCanvas<?> html) {
        html.input_text().VALUE(value);
    }
}
