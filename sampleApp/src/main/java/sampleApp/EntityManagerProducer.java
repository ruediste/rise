package sampleApp;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.*;

import com.github.ruediste.laf.core.persistence.PersistenceUnitToken;
import com.github.ruediste.laf.core.persistence.PersistenceUnitTokenManager;

public class EntityManagerProducer {
	@Inject
	PersistenceUnitTokenManager contextManager;

	@PersistenceUnit
	EntityManagerFactory factory;

	private PersistenceUnitToken token;

	@PostConstruct
	public void initialize() {
		token = contextManager.createToken("", factory);
	}

	@Produces
	@ApplicationScoped
	EntityManager produceManager() {
		return contextManager.createProvider(token);
	}
}
