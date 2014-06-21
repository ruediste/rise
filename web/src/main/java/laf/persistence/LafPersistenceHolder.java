package laf.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;

/**
 * Represents a map of {@link LafEntityManagerFactory}s to the corresponding
 * {@link EntityManager}s
 */
public class LafPersistenceHolder {

	private Map<LafEntityManagerFactory, EntityManager> entityManagers = new HashMap<LafEntityManagerFactory, EntityManager>();

	public EntityManager getEntityManager(LafEntityManagerFactory factory) {

		EntityManager result = entityManagers.get(factory);
		if (result == null) {
			result = factory.createEntityManager();
			entityManagers.put(factory, result);
		}
		return result;
	}

	@PreDestroy
	public void destroy() {
		for (EntityManager em : entityManagers.values()) {
			em.close();
		}
	}

	public void joinTransaction() {
		for (EntityManager em : entityManagers.values()) {
			em.joinTransaction();
		}
	}
}
