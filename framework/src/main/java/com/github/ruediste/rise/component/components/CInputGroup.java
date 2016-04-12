package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;

@DefaultTemplate(CInputGroupTemplate.class)
public class CInputGroup extends MultiChildrenComponent<CInputGroup> {
    public CInputGroup addon(Component addon) {
        return add(new CInputGroupAddon(addon));
    }

}
