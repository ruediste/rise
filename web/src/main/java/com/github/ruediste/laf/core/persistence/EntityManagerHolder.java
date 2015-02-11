package com.github.ruediste.laf.core.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.laf.core.base.LafLogger;

/**
 * Represents a map of {@link PersistenceUnitToken}s to the corresponding
 * {@link EntityManager}s. Used to hold currently open {@link EntityManager}
 * instances.
 */
public class EntityManagerHolder {
	@Inject
	LafLogger log;

	@Inject
	PersistenceUnitTokenManager manager;

	private Map<PersistenceUnitToken, EntityManager> entityManagers = new HashMap<>();

	public EntityManager getEntityManager(PersistenceUnitToken token) {

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
			if (em.isOpen()) {
				em.close();
			}
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

	public PersistenceUnitToken getTokenByEntity(Object value) {
		return entityManagers.entrySet().stream()
				.filter(x -> x.getValue().contains(value)).map(x -> x.getKey())
				.findFirst().orElse(null);
	}
}
