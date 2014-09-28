package laf.core.persistence;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.slf4j.Logger;

import com.google.common.base.Supplier;

@ApplicationScoped
public class LafPersistenceContextManager {

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

	private final Map<EntityManagerToken, TokenEntry> tokenMap = new HashMap<>();
	private final Map<Set<Class<? extends Annotation>>, EntityManagerToken> qualifierMap = new HashMap<>();

	/**
	 * To be used by application provided {@link EntityManager} producer
	 * methods.
	 */
	public EntityManager produceManager(EntityManagerToken token) {
		return new TokenBasedDelegatingEntityManager(this, token);
	}

	EntityManager createNewEntityManager(EntityManagerToken token) {
		return tokenMap.get(token).supplier.get();
	}

	public EntityManagerToken getToken(
			List<Class<? extends Annotation>> qualifiers) {
		return qualifierMap.get(new HashSet<>(qualifiers));
	}

	public Metamodel getMetamodel(EntityManagerToken token) {
		return tokenMap.get(token).factory.getMetamodel();
	}

	public PersistenceUnitUtil getPersistenceUnitUtil(EntityManagerToken token) {
		return tokenMap.get(token).factory.getPersistenceUnitUtil();
	}

	public EntityType<?> getEntity(EntityManagerToken token,
			Class<?> entityClass) {
		return tokenMap.get(token).classToEntityMap.get(entityClass);
	}

	public EntityManagerToken createToken(
			EntityManagerFactory factory,
			@SuppressWarnings("unchecked") Class<? extends Annotation>... qualifiers) {
		return createToken(factory, factory::createEntityManager, qualifiers);
	}

	public EntityManagerToken createToken(
			EntityManagerFactory factory,
			Supplier<EntityManager> supplier,
			@SuppressWarnings("unchecked") Class<? extends Annotation>... qualifiers) {
		EntityManagerToken token = new EntityManagerToken();
		tokenMap.put(token, new TokenEntry(factory, supplier));
		qualifierMap.put(new HashSet<>(Arrays.asList(qualifiers)), token);
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
