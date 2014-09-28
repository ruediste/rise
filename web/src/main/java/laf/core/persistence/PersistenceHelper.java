package laf.core.persistence;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public class PersistenceHelper {

	@Inject
	EntityManager manager;

	public <T> List<T> loadAll(Class<T> entityClass) {
		return loadAll(manager, entityClass);
	}

	public <T> List<T> loadAll(EntityManager em, Class<T> entityClass) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> q = cb.createQuery(entityClass);
		q.from(entityClass);
		return em.createQuery(q).getResultList();
	}
}
