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

	public List<TestEntity> getAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestEntity> query = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = query.from(TestEntity.class);
		query.select(root);
		return em.createQuery(query).getResultList();
	}
}
