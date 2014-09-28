package laf.core.persistence;

import javax.persistence.EntityManager;

/**
 * {@link EntityManager} implementation associated with a
 * {@link EntityManagerToken} which always delegates to the
 * {@link EntityManager} provided by the {@link LafPersistenceHolder} returned
 * by {@link LafPersistenceContextManager#getCurrentHolder()}
 */
class TokenBasedDelegatingEntityManager extends DelegatingEntityManager {

	public static class NoPersistenceContextException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public NoPersistenceContextException() {
			super("No Persistence Context is active. Use "
					+ LafPersistenceContextManager.class.getSimpleName()
					+ " to make one active");
		}
	}

	private LafPersistenceContextManager manager;
	private EntityManagerToken token;

	TokenBasedDelegatingEntityManager(LafPersistenceContextManager manager,
			EntityManagerToken token) {
		this.manager = manager;
		this.token = token;

	}

	@Override
	protected EntityManager delegate() {
		LafPersistenceHolder holder = manager.getCurrentHolder();
		if (holder == null) {
			throw new NoPersistenceContextException();
		}
		return holder.getEntityManager(token);
	}

}
