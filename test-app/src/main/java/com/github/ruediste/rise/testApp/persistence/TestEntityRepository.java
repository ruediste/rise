package com.github.ruediste.rise.testApp.persistence;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class TestEntityRepository {

    @Inject
    EntityManager em;

    public List<TestAppEntity> getAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TestAppEntity> query = cb
                .createQuery(TestAppEntity.class);
        Root<TestAppEntity> root = query.from(TestAppEntity.class);
        query.select(root);
        return em.createQuery(query).getResultList();
    }
}
