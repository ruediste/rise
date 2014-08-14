package laf.core.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

import com.google.common.base.Supplier;

@ApplicationScoped
public class LafPersistenceContextManager {

	@Inject
	Logger log;

	/**
	 * To be used by application provided {@link EntityManager} producer
	 * methods.
	 */
	public EntityManager produceManagerDelegate(EntityManagerSupplierToken token) {
		return new TokenBasedDelegatingEntityManager(this, token);
	}

	private final Map<EntityManagerSupplierToken, Supplier<EntityManager>> supplierMap = new HashMap<>();

	public EntityManager produceEntityManager(EntityManagerSupplierToken token) {
		return supplierMap.get(token).get();
	}

	public EntityManagerSupplierToken registerEntityManagerSupplier(
			Supplier<EntityManager> supplier) {
		EntityManagerSupplierToken token = new EntityManagerSupplierToken();
		supplierMap.put(token, supplier);
		return token;
	}

	private final ThreadLocal<LafPersistenceHolder> currentHolder = new ThreadLocal<>();

	public LafPersistenceHolder getCurrentHolder() {
		return currentHolder.get();
	}

	public void withPersistenceHolder(LafPersistenceHolder holder,
			Runnable runnable) {
		LafPersistenceHolder oldHolder = currentHolder.get();
		try {
			log.debug("entering holder " + holder.toString());
			currentHolder.set(holder);
			runnable.run();
		} finally {
			log.debug("leaving holder " + holder.toString());
			currentHolder.set(oldHolder);
		}
	}
}
