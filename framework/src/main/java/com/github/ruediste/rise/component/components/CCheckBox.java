package com.github.ruediste.rise.component.components;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Show a check box
 */
@DefaultTemplate(CCheckBoxTemplate.class)
public class CCheckBox extends CInputBase<CCheckBox> {

    private Optional<Consumer<Boolean>> toggledHandler = Optional.empty();

    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Optional<Consumer<Boolean>> getToggledHandler() {
        return toggledHandler;
    }

    public CCheckBox setToggledHandler(Consumer<Boolean> toggledHandler) {
        this.toggledHandler = Optional.of(toggledHandler);
        return this;
    }

}
