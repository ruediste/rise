package laf.requestProcessing;

import java.io.Serializable;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

public class EntityManagerProducer implements Serializable {
	private static final long serialVersionUID = 1L;

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
