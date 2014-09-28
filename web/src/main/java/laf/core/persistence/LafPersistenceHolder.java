package laf.core.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import laf.core.base.LafLogger;

/**
 * Represents a map of {@link EntityManagerToken}s to the corresponding
 * {@link EntityManager}s
 */
@Typed(LafPersistenceHolder.class)
public class LafPersistenceHolder {
	@Inject
	LafLogger log;

	@Inject
	LafPersistenceContextManager manager;

	private Map<EntityManagerToken, EntityManager> entityManagers = new HashMap<>();

	public EntityManager getEntityManager(EntityManagerToken token) {

		EntityManager result = entityManagers.get(token);
		if (result == null) {
			result = manager.createNewEntityManager(token);
			log.debug("Created entity manager " + result + " for token "
					+ token + " in persistence holder " + this);
			entityManagers.put(token, result);
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

	public void flush() {
		for (EntityManager em : entityManagers.values()) {
			em.flush();
		}

	}
}
