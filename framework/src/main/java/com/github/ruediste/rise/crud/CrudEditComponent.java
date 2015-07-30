package com.github.ruediste.rise.crud;

import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.tree.Component;

public interface CrudEditComponent {

    CrudEditComponent initialize(BindingGroup<?> group);

    Component getComponent();
}
