package laf.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

@ApplicationScoped
public class LafPersistenceContextManager {

	@Inject
	Logger log;

	/**
	 * To be used by application provided {@link EntityManager} producer
	 * methods.
	 */
	public EntityManager produceManagerDelegate(LafEntityManagerFactory factory) {
		return new LafEntityManager(this, factory);
	}

	private final ThreadLocal<LafPersistenceHolderBase> currentHolder = new ThreadLocal<>();

	public LafPersistenceHolderBase getCurrentHolder() {
		return currentHolder.get();
	}

	public void withPersistenceHolder(LafPersistenceHolderBase holder,
			Runnable runnable) {
		LafPersistenceHolderBase oldHolder = currentHolder.get();
		try {
			log.debug("entering holder " + holder.implToString());
			currentHolder.set(holder);
			runnable.run();
		} finally {
			log.debug("leaving holder " + holder.implToString());
			currentHolder.set(oldHolder);
		}
	}
}
