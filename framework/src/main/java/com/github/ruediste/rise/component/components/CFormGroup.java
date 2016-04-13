package com.github.ruediste.rise.component.components;

import java.util.Optional;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.validation.ValidationStatusPresenter;
import com.github.ruediste1.i18n.lString.LString;

@DefaultTemplate(CFormGroupTemplate.class)
public class CFormGroup extends MultiChildrenComponent<CFormGroup>implements ValidationStatusPresenter {

    private Optional<? extends LString> label = Optional.empty();

    public CFormGroup() {
    }

    public CFormGroup(Component child) {
        add(child);
    }

    public Optional<? extends LString> getLabel() {
        return label;
    }

    public CFormGroup setLabel(Optional<? extends LString> label) {
        this.label = label;
        return this;
    }
}
