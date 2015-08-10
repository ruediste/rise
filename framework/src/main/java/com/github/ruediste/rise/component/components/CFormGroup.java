package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;

@DefaultTemplate(CFormGroupTemplate.class)
public class CFormGroup extends MultiChildrenComponent<CFormGroup> {

    public CFormGroup() {
    }

    public CFormGroup(Component child) {
        add(child);
    }
}
