package laf.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import laf.persistence.LafEntityManagerFactory;
import laf.persistence.LafPersistenceContextManager;

public class TestEntityManagerProducer {
	@Inject
	LafPersistenceContextManager contextManager;

	@PersistenceUnit
	EntityManagerFactory factory;

	@Produces
	@ApplicationScoped
	EntityManager produceManager() {
		return contextManager
				.produceManagerDelegate(new LafEntityManagerFactory() {

					@Override
					public EntityManager createEntityManager() {
						return factory.createEntityManager();
					}
				});
	}
}
