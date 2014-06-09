package laf.component.core;

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import laf.requestProcessing.EntityManagerProducer;

@Stateful
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PagePersistenceManager implements Serializable {

	private static final long serialVersionUID = 1L;

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
	public void withManager(Runnable runnable) {
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
