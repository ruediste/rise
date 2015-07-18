package com.github.ruediste.rise.crud;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;

import com.github.ruediste.rise.component.tree.Component;

public interface CrudPropertyFilter {

    interface CrudFilterPersitenceContext {
        CriteriaBuilder cb();

        Query query();
    }

    Component getComponent();

    void applyFilter(CrudFilterPersitenceContext ctx);
}
