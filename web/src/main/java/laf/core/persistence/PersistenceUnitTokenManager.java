package laf.core.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;

import org.slf4j.Logger;

import com.google.common.base.Supplier;

@ApplicationScoped
public class PersistenceUnitTokenManager {

	public static class NoPersistenceContextException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public NoPersistenceContextException() {
			super("No Persistence Context is active. Use "
					+ PersistenceUnitTokenManager.class.getSimpleName()
					+ " to make one active");
		}
	}

	@Inject
	Logger log;

	private static class TokenEntry {
		Supplier<EntityManager> supplier;
		EntityManagerFactory factory;

		HashMap<Class<?>, EntityType<?>> classToEntityMap = new HashMap<>();

		public TokenEntry(EntityManagerFactory factory,
				Supplier<EntityManager> supplier) {
			super();
			this.supplier = supplier;
			this.factory = factory;
			factory.getMetamodel().getEntities()
					.forEach(e -> classToEntityMap.put(e.getJavaType(), e));
		}

	}

	private final Map<PersistenceUnitToken, TokenEntry> tokenMap = new HashMap<>();
	private final Map<String, PersistenceUnitToken> persistenceUnitNameToTokenMap = new HashMap<>();

	/**
	 * To be used by application provided {@link EntityManager} producer
	 * methods.
	 */
	public EntityManager produceManager(PersistenceUnitToken token) {
		return (EntityManager) Enhancer.create(EntityManager.class,
				new Dispatcher() {

					@Override
					public Object loadObject() throws Exception {
						// always delegate to the entity manager of the current
						// holder
						LafPersistenceHolder holder = getCurrentHolder();
						if (holder == null) {
							throw new NoPersistenceContextException();
						}
						return holder.getEntityManager(token);
					}
				});
	}

	/**
	 * Used by the {@link LafPersistenceHolder} to create entity managers if
	 * necessary
	 */
	EntityManager createNewEntityManager(PersistenceUnitToken token) {
		return tokenMap.get(token).supplier.get();
	}

	public PersistenceUnitToken getToken(String persistenceUnitName) {
		return persistenceUnitNameToTokenMap.get(persistenceUnitName);
	}

	public Metamodel getMetamodel(PersistenceUnitToken token) {
		return tokenMap.get(token).factory.getMetamodel();
	}

	public PersistenceUnitUtil getPersistenceUnitUtil(PersistenceUnitToken token) {
		return tokenMap.get(token).factory.getPersistenceUnitUtil();
	}

	public EntityType<?> getEntityMetaModel(PersistenceUnitToken token,
			Class<?> entityClass) {
		return tokenMap.get(token).classToEntityMap.get(entityClass);
	}

	/**
	 * Create an {@link PersistenceUnitToken}. To be used by entity manager
	 * producers.
	 */
	public PersistenceUnitToken createToken(String persistenceUnitName) {
		return createToken(persistenceUnitName,
				Persistence.createEntityManagerFactory(persistenceUnitName));
	}

	/**
	 * Create an {@link PersistenceUnitToken}. To be used by entity manager
	 * producers.
	 */
	public PersistenceUnitToken createToken(String persistenceUnitName,
			Map<String, ? extends Object> properties) {
		return createToken(persistenceUnitName,
				Persistence.createEntityManagerFactory(persistenceUnitName,
						properties));
	}

	/**
	 * Create an {@link PersistenceUnitToken}. To be used by entity manager
	 * producers. Used if multiple {@link EntityManager}s are used in a single
	 * application.
	 */
	public PersistenceUnitToken createToken(String persistenceUnitName,
			EntityManagerFactory factory) {
		return createToken(persistenceUnitName, factory,
				factory::createEntityManager);
	}

	public PersistenceUnitToken createToken(String persistenceUnitName,
			EntityManagerFactory factory, Supplier<EntityManager> supplier) {
		PersistenceUnitToken token = new PersistenceUnitToken(
				persistenceUnitName);
		if (tokenMap.put(token, new TokenEntry(factory, supplier)) != null) {
			throw new RuntimeException("the persistence unit "
					+ persistenceUnitName + " is already registered");
		}
		persistenceUnitNameToTokenMap.put(persistenceUnitName, token);
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
