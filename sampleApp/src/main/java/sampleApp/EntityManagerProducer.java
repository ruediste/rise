package sampleApp;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.*;

import laf.core.persistence.EntityManagerSupplierToken;
import laf.core.persistence.LafPersistenceContextManager;

public class EntityManagerProducer {
	@Inject
	LafPersistenceContextManager contextManager;

	@PersistenceUnit
	EntityManagerFactory factory;

	private EntityManagerSupplierToken token;

	@PostConstruct
	public void initialize() {
		token = contextManager.registerEntityManagerSupplier(() -> factory
				.createEntityManager());
	}

	@Produces
	@ApplicationScoped
	EntityManager produceManager() {
		return contextManager.produceManagerDelegate(token);
	}
}
