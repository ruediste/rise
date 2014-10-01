package laf.skeleton;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.*;

import laf.core.persistence.EntityManagerToken;
import laf.core.persistence.LafPersistenceContextManager;

/**
 * Producer of the {@link EntityManager}s which are available in the
 * application. Allows handling multiple databases.
 */
public class ApplicationEntityManagerProducer {
	@Inject
	LafPersistenceContextManager contextManager;

	@PersistenceUnit
	EntityManagerFactory factory;

	private EntityManagerToken token;

	@PostConstruct
	public void initialize() {
		token = contextManager.createToken(factory);
	}

	@Produces
	@ApplicationScoped
	EntityManager produceManager() {
		return contextManager.produceManager(token);
	}
}
