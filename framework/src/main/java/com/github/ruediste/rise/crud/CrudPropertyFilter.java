package com.github.ruediste.rise.crud;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.github.ruediste.rise.component.tree.Component;

public interface CrudPropertyFilter {

    interface CrudFilterPersitenceContext {
        CriteriaBuilder cb();

        CriteriaQuery<?> query();

        Root<?> root();

        void addWhere(Predicate predicate);
    }

    Component getComponent();

    void applyFilter(CrudFilterPersitenceContext ctx);
}
