package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;

@DefaultTemplate(CCheckBoxTemplate.class)
public class CCheckBox extends ComponentBase<CButton> {

    @Inject
    ComponentUtil util;

    private Supplier<Boolean> value;

    public CCheckBox(@Capture Supplier<Boolean> value) {
        this.value = value;
    }

    public Supplier<Boolean> getValue() {
        return value;
    }

    public void setValue(Supplier<Boolean> value) {
        this.value = value;
    }

}
