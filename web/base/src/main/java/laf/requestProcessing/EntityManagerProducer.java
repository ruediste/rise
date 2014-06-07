package laf.requestProcessing;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

public class EntityManagerProducer {
	ThreadLocal<EntityManager> manager = new ThreadLocal<>();

	@Produces
	private final DelegateEntityManager delegate = new DelegateEntityManager() {

		@Override
		protected EntityManager delegate() {
			return manager.get();
		}
	};

	public void withManager(EntityManager manager, Runnable runnable) {
		this.manager.set(manager);
		try {
			runnable.run();
		} finally {
			this.manager.set(null);
		}
	}
}
