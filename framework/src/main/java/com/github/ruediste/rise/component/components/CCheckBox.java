package com.github.ruediste.rise.component.components;

import java.util.Optional;
import java.util.function.Consumer;

import com.github.ruediste.c3java.properties.NoPropertyField;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.MultiChildrenRelation;

/**
 * Show a check box
 */
@DefaultTemplate(CCheckBoxTemplate.class)
public class CCheckBox extends CInputBase<CCheckBox> {

    @NoPropertyField
    public final MultiChildrenRelation<Component, CCheckBox> label = new MultiChildrenRelation<>(this);

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

    public CCheckBox add(Component... labelComponents) {
        return label.add(labelComponents);
    }
}
