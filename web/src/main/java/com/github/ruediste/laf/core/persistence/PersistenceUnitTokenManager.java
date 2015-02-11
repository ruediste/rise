package com.github.ruediste.laf.core.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;

import org.slf4j.Logger;

import com.google.common.base.Supplier;

@Singleton
public class PersistenceUnitTokenManager {
	Logger log;

	public static class NoPersistenceContextException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public NoPersistenceContextException() {
			super("No Persistence Context is active. Use "
					+ PersistenceUnitTokenManager.class.getSimpleName()
					+ " to make one active");
		}
	}

	private static class TokenEntry {
		private final Supplier<EntityManagerFactory> factorySupplier;

		private EntityManagerFactory factory;

		private HashMap<Class<?>, EntityType<?>> classToEntityMap;
		private final Map<String, Object> managerProperties;

		public TokenEntry(Supplier<EntityManagerFactory> factorySupplier,
				Map<String, Object> managerProperties) {
			this.factorySupplier = factorySupplier;
			this.managerProperties = managerProperties;

		}

		public EntityManagerFactory getFactory() {
			if (factory == null) {
				synchronized (this) {
					if (factory == null) {
						factory = factorySupplier.get();
					}
				}
			}
			return factory;
		}

		public Map<Class<?>, EntityType<?>> getClassToEntityMap() {
			if (classToEntityMap == null) {
				synchronized (this) {
					if (classToEntityMap == null) {
						classToEntityMap = new HashMap<>();
						getFactory()
								.getMetamodel()
								.getEntities()
								.forEach(
										e -> classToEntityMap.put(
												e.getJavaType(), e));
					}
				}
			}
			return classToEntityMap;
		}

		public Map<String, Object> getManagerProperties() {
			return managerProperties;
		}

	}

	private final Map<PersistenceUnitToken, TokenEntry> tokenMap = new HashMap<>();
	private final Map<String, PersistenceUnitToken> persistenceUnitNameToTokenMap = new HashMap<>();

	private final ThreadLocal<EntityManagerHolder> currentHolder = new ThreadLocal<>();

	public class TokenBuilder {
		final String persistenceUnitName;
		Supplier<EntityManagerFactory> factorySupplier;
		Map<String, Object> managerProperties;

		public TokenBuilder(String persistenceUnitName) {
			this.persistenceUnitName = persistenceUnitName;
			factorySupplier = () -> Persistence
					.createEntityManagerFactory(persistenceUnitName);
		}

		public TokenBuilder withEntityManagerProperties(
				Map<String, Object> managerProperties) {
			this.managerProperties = managerProperties;
			return this;
		}

		public PersistenceUnitToken build() {
			PersistenceUnitToken token = new PersistenceUnitToken(
					persistenceUnitName);
			if (tokenMap.put(token, new TokenEntry(factorySupplier,
					managerProperties)) != null) {
				throw new RuntimeException("the persistence unit "
						+ persistenceUnitName + " is already registered");
			}
			persistenceUnitNameToTokenMap.put(persistenceUnitName, token);
			return token;
		}
	}

	/**
	 * To be used by application provided {@link EntityManager} producer
	 * methods.
	 */
	public Provider<EntityManager> createProvider(PersistenceUnitToken token) {
		Object proxy = Enhancer.create(EntityManager.class, new Dispatcher() {

			@Override
			public Object loadObject() throws Exception {
				// always delegate to the entity manager of the current
				// holder
				EntityManagerHolder holder = getCurrentHolder();
				if (holder == null) {
					throw new NoPersistenceContextException();
				}
				return holder.getEntityManager(token);
			}
		});
		return () -> (EntityManager) proxy;
	}

	/**
	 * Used by the {@link EntityManagerHolder} to create entity managers if
	 * necessary
	 */
	public EntityManager createNewEntityManager(PersistenceUnitToken token) {
		TokenEntry tokenEntry = tokenMap.get(token);
		EntityManagerFactory factory = tokenEntry.getFactory();
		if (tokenEntry.getManagerProperties() == null) {
			return factory.createEntityManager();
		} else {
			return factory.createEntityManager(tokenEntry
					.getManagerProperties());
		}
	}

	public PersistenceUnitToken getToken(String persistenceUnitName) {
		return persistenceUnitNameToTokenMap.get(persistenceUnitName);
	}

	public Metamodel getMetamodel(PersistenceUnitToken token) {
		return tokenMap.get(token).getFactory().getMetamodel();
	}

	public PersistenceUnitUtil getPersistenceUnitUtil(PersistenceUnitToken token) {
		return tokenMap.get(token).getFactory().getPersistenceUnitUtil();
	}

	public EntityType<?> getEntityMetaModel(PersistenceUnitToken token,
			Class<?> entityClass) {
		return tokenMap.get(token).getClassToEntityMap().get(entityClass);
	}

	/**
	 * Create an {@link PersistenceUnitToken}. To be used by entity manager
	 * producers.
	 */
	public TokenBuilder createToken(String persistenceUnitName) {
		return new TokenBuilder(persistenceUnitName);
	}

	public EntityManagerHolder getCurrentHolder() {
		return currentHolder.get();
	}

	public void withPersistenceHolder(EntityManagerHolder holder,
			Runnable runnable) {
		EntityManagerHolder oldHolder = currentHolder.get();
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
