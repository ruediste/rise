package laf.test;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.*;

import laf.core.persistence.EntityManagerSupplierToken;
import laf.core.persistence.LafPersistenceContextManager;

import com.google.common.base.Supplier;

public class TestEntityManagerProducer {
	@Inject
	LafPersistenceContextManager contextManager;

	@PersistenceUnit
	EntityManagerFactory factory;

	private EntityManagerSupplierToken token;

	@PostConstruct
	public void initialize() {
		token = contextManager
				.registerEntityManagerSupplier(new Supplier<EntityManager>() {

					@Override
					public EntityManager get() {
						return factory.createEntityManager();
					}
				});
	}

	@Produces
	@ApplicationScoped
	EntityManager produceManager() {
		return contextManager.produceManagerDelegate(token);
	}
}
