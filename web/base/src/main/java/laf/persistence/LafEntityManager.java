package laf.persistence;

import java.util.Map;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

/**
 * {@link EntityManager} implementation associated with a
 * {@link LafEntityManagerFactory} which always delegates to the
 * {@link EntityManager} provided by the {@link LafPersistenceHolder} returned
 * by {@link LafPersistenceContextManager#getCurrentHolder()}
 */
class LafEntityManager implements EntityManager {

	public static class NoPersistenceContextException extends RuntimeException {
		public NoPersistenceContextException() {
			super("No Persistence Context is active. Use "
					+ LafPersistenceContextManager.class.getSimpleName()
					+ " to make one active");
		}
	}

	private LafPersistenceContextManager manager;
	private LafEntityManagerFactory factory;

	LafEntityManager(LafPersistenceContextManager manager,
			LafEntityManagerFactory factory) {
		this.manager = manager;
		this.factory = factory;

	}

	private EntityManager delegate() {
		LafPersistenceHolder delegate = manager.getCurrentHolder();
		if (delegate == null) {
			throw new NoPersistenceContextException();
		}
		return delegate.getEntityManager(factory);
	}

	@Override
	public void persist(Object entity) {
		delegate().persist(entity);
	}

	@Override
	public <T> T merge(T entity) {
		return delegate().merge(entity);
	}

	@Override
	public void remove(Object entity) {
		delegate().remove(entity);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		return delegate().find(entityClass, primaryKey);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey,
			Map<String, Object> properties) {
		return delegate().find(entityClass, primaryKey, properties);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey,
			LockModeType lockMode) {
		return delegate().find(entityClass, primaryKey, lockMode);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey,
			LockModeType lockMode, Map<String, Object> properties) {
		return delegate().find(entityClass, primaryKey, lockMode, properties);
	}

	@Override
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		return delegate().getReference(entityClass, primaryKey);
	}

	@Override
	public void flush() {
		delegate().flush();
	}

	@Override
	public void setFlushMode(FlushModeType flushMode) {
		delegate().setFlushMode(flushMode);
	}

	@Override
	public FlushModeType getFlushMode() {
		return delegate().getFlushMode();
	}

	@Override
	public void lock(Object entity, LockModeType lockMode) {
		delegate().lock(entity, lockMode);
	}

	@Override
	public void lock(Object entity, LockModeType lockMode,
			Map<String, Object> properties) {
		delegate().lock(entity, lockMode, properties);
	}

	@Override
	public void refresh(Object entity) {
		delegate().refresh(entity);
	}

	@Override
	public void refresh(Object entity, Map<String, Object> properties) {
		delegate().refresh(entity, properties);
	}

	@Override
	public void refresh(Object entity, LockModeType lockMode) {
		delegate().refresh(entity, lockMode);
	}

	@Override
	public void refresh(Object entity, LockModeType lockMode,
			Map<String, Object> properties) {
		delegate().refresh(entity, lockMode, properties);
	}

	@Override
	public void clear() {
		delegate().clear();
	}

	@Override
	public void detach(Object entity) {
		delegate().detach(entity);
	}

	@Override
	public boolean contains(Object entity) {
		return delegate().contains(entity);
	}

	@Override
	public LockModeType getLockMode(Object entity) {
		return delegate().getLockMode(entity);
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		delegate().setProperty(propertyName, value);
	}

	@Override
	public Map<String, Object> getProperties() {
		return delegate().getProperties();
	}

	@Override
	public Query createQuery(String qlString) {
		return delegate().createQuery(qlString);
	}

	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		return delegate().createQuery(criteriaQuery);
	}

	@Override
	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		return delegate().createQuery(qlString, resultClass);
	}

	@Override
	public Query createNamedQuery(String name) {
		return delegate().createNamedQuery(name);
	}

	@Override
	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
		return delegate().createNamedQuery(name, resultClass);
	}

	@Override
	public Query createNativeQuery(String sqlString) {
		return delegate().createNativeQuery(sqlString);
	}

	@Override
	public Query createNativeQuery(String sqlString,
			@SuppressWarnings("rawtypes") Class resultClass) {
		return delegate().createNativeQuery(sqlString, resultClass);
	}

	@Override
	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		return delegate().createNativeQuery(sqlString, resultSetMapping);
	}

	@Override
	public void joinTransaction() {
		delegate().joinTransaction();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		return delegate().unwrap(cls);
	}

	@Override
	public Object getDelegate() {
		return delegate().getDelegate();
	}

	@Override
	public void close() {
		delegate().close();
	}

	@Override
	public boolean isOpen() {
		return delegate().isOpen();
	}

	@Override
	public EntityTransaction getTransaction() {
		return delegate().getTransaction();
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		return delegate().getEntityManagerFactory();
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		return delegate().getCriteriaBuilder();
	}

	@Override
	public Metamodel getMetamodel() {
		return delegate().getMetamodel();
	}

}
