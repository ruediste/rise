package laf.persistence;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.*;

import laf.persistence.LafEntityManager.NoPersistenceContextException;
import laf.test.BaseDeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class LafPersistenceContextManagerTest {

	@Deployment
	public static WebArchive getDeployment() {
		WebArchive result = BaseDeploymentProvider.getPersistence().addClasses(
				Modules.getAllRequiredClasses(PersistenceModule.class));
		System.out.println(result.toString(true));
		return result;
	}

	@ApplicationScoped
	static class EMProducer {
		@Inject
		LafPersistenceContextManager contextManager;

		@PersistenceUnit
		EntityManagerFactory factory;

		EntityManager manager;

		@PostConstruct
		public void initialize() {
			manager = contextManager
					.produceManagerDelegate(new LafEntityManagerFactory() {

						@Override
						public EntityManager createEntityManager() {
							return factory.createEntityManager();
						}
					});
		}

		@Produces
		EntityManager produceManager() {
			return manager;
		}
	}

	static class TestBean {
		@Inject
		EntityManager manager;
	}

	@Inject
	TestBean bean;

	@Inject
	LafPersistenceContextManager contextManager;

	@Test(expected = NoPersistenceContextException.class)
	public void errorNoPersistenceContext() {
		bean.manager.getFlushMode();
	}

	@Inject
	LafPersistenceHolder holder;

	@Test
	public void simple() {
		contextManager.withPersistenceHolder(holder, new Runnable() {

			@Override
			public void run() {
				bean.manager.getFlushMode();
			}
		});
	}
}
