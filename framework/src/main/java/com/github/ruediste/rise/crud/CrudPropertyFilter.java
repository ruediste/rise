package com.github.ruediste.rise.crud;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.crud.CrudUtil.PersistenceFilterContext;

public interface CrudPropertyFilter {

    Component getComponent();

    void applyFilter(PersistenceFilterContext<?> ctx);
}
