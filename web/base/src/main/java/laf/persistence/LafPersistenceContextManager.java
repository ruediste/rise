package laf.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;

@ApplicationScoped
public class LafPersistenceContextManager {

	/**
	 * To be used by application provided {@link EntityManager} producer
	 * methods.
	 */
	public EntityManager produceManagerDelegate(LafEntityManagerFactory factory) {
		return new LafEntityManager(this, factory);
	}

	private final ThreadLocal<LafPersistenceHolder> currentHolder = new ThreadLocal<>();

	public LafPersistenceHolder getCurrentHolder() {
		return currentHolder.get();
	}

	public void withPersistenceHolder(LafPersistenceHolder holder,
			Runnable runnable) {
		LafPersistenceHolder oldHolder = currentHolder.get();
		try {
			currentHolder.set(holder);
			runnable.run();
		} finally {
			currentHolder.set(oldHolder);
		}
	}
}
