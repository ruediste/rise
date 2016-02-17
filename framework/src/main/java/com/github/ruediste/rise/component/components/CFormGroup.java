package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.validation.ValidationStatusPresenter;

@DefaultTemplate(CFormGroupTemplate.class)
public class CFormGroup extends MultiChildrenComponent<CFormGroup>
        implements ValidationStatusPresenter {

    public CFormGroup() {
    }

    public CFormGroup(Component child) {
        add(child);
    }
}
