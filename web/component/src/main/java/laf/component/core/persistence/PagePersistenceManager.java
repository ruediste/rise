package laf.component.core.persistence;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.*;

import laf.requestProcessing.EntityManagerProducer;

@Stateful
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PagePersistenceManager {

	@PersistenceUnit
	EntityManagerFactory entityManagerFactory;

	@Inject
	EntityManagerProducer entityManagerProducer;

	private EntityManager manager;

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void initialize(Runnable runnable) {
		manager = entityManagerFactory.createEntityManager();
		entityManagerProducer.withManager(manager, runnable);
	}

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void reload(Runnable runnable) {
		entityManagerProducer.withManager(manager, runnable);
	}

	public void commit() {
		commit(null, null);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void commit(Runnable check, Runnable inTransaction) {
		if (check != null) {
			EntityManager checkManager = entityManagerFactory
					.createEntityManager();

			entityManagerProducer.withManager(checkManager, check);
		}
		manager.joinTransaction();
		if (inTransaction != null) {
			entityManagerProducer.withManager(manager, inTransaction);
		}
	}

	@TransactionAttribute(TransactionAttributeType.NEVER)
	@Remove
	public void remove() {
		manager.close();
	}
}
