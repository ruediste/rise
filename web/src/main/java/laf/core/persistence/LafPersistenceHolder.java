package laf.core.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import laf.base.LafLogger;

/**
 * Represents a map of {@link LafEntityManagerFactory}s to the corresponding
 * {@link EntityManager}s
 */
@Typed(LafPersistenceHolder.class)
public class LafPersistenceHolder {
	@Inject
	LafLogger log;

	private Map<LafEntityManagerFactory, EntityManager> entityManagers = new HashMap<LafEntityManagerFactory, EntityManager>();

	public EntityManager getEntityManager(LafEntityManagerFactory factory) {

		EntityManager result = entityManagers.get(factory);
		if (result == null) {
			result = factory.createEntityManager();
			log.debug("Created entity manager " + result + " for factory "
					+ factory + " in persistence holder " + this);
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

	public void flush() {
		for (EntityManager em : entityManagers.values()) {
			em.flush();
		}

	}

	public String implToString() {
		return toString();
	}
}
