package laf.persistence;

import static org.junit.Assert.assertSame;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

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

	static class EMProducer {
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

	static class TestBean {
		@Inject
		EntityManager manager;
	}

	@Inject
	TestBean bean;

	@Inject
	LafPersistenceContextManager contextManager;

	@Inject
	LafPersistenceHolder holder;

	@Test(expected = NoPersistenceContextException.class)
	public void errorNoPersistenceContext() {
		bean.manager.getFlushMode();
	}

	@Test
	public void simple() {
		contextManager.withPersistenceHolder(holder, new Runnable() {

			@Override
			public void run() {
				bean.manager.getFlushMode();
			}
		});
	}

	@Inject
	Instance<EntityManager> entityManagerInstance;

	@Test
	public void repeatedRetrieval() {
		EntityManager em1 = entityManagerInstance.get();
		EntityManager em2 = entityManagerInstance.get();
		assertSame(em1, em2);
	}
}
