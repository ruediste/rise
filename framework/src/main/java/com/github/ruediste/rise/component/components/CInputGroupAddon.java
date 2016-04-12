package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;

@DefaultTemplate(CInputGroupAddonTemplate.class)
public class CInputGroupAddon extends MultiChildrenComponent<CInputGroupAddon> {

    public CInputGroupAddon() {
    }

    public CInputGroupAddon(Component child) {
        add(child);
    }

}
