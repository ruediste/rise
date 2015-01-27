package laf.testApp;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.*;

import laf.core.persistence.PersistenceUnitToken;
import laf.core.persistence.PersistenceUnitTokenManager;

public class TestAppEntityManagerProducer {
	@Inject
	PersistenceUnitTokenManager contextManager;

	@PersistenceUnit
	EntityManagerFactory factory;

	private PersistenceUnitToken token;

	@PostConstruct
	public void initialize() {
		token = contextManager.createToken("test-unit");
	}

	@Produces
	@ApplicationScoped
	EntityManager produceManager() {
		return contextManager.produceManager(token);
	}
}
