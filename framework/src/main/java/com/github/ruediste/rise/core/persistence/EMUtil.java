package com.github.ruediste.rise.core.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class EMUtil {

    public interface PersistenceFilterContext<T> {
        CriteriaBuilder cb();

        CriteriaQuery<T> query();

        Root<T> root();

        void addWhere(Predicate predicate);
    }

    public static <T> List<T> loadAll(EntityManager em, Class<T> entityClass) {
        return queryWithFilter(em, entityClass, x -> {
        }).getResultList();
    }

    public static <T> TypedQuery<T> queryWithFilter(EntityManager em, Class<T> entityClass,
            Consumer<PersistenceFilterContext<T>> action) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(entityClass);
        Root<T> root = q.from(entityClass);
        q.select(root);

        ArrayList<Predicate> whereClauses = new ArrayList<>();
        PersistenceFilterContext<T> filterContext = new PersistenceFilterContext<T>() {

            @Override
            public CriteriaQuery<T> query() {
                return q;
            }

            @Override
            public CriteriaBuilder cb() {
                return cb;
            }

            @Override
            public Root<T> root() {
                return root;
            }

            @Override
            public void addWhere(Predicate predicate) {
                whereClauses.add(predicate);
            }
        };

        action.accept(filterContext);

        q.where(whereClauses.toArray(new Predicate[] {}));
        return em.createQuery(filterContext.query());
    }

}
