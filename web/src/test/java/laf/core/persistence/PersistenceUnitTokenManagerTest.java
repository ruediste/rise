package laf.core.persistence;

import static org.junit.Assert.assertSame;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import laf.core.persistence.PersistenceUnitTokenManager.NoPersistenceContextException;
import laf.test.DeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PersistenceUnitTokenManagerTest {

	@Deployment
	public static WebArchive getDeployment() {
		WebArchive result = DeploymentProvider.getPersistence().addClasses(
				Modules.getAllRequiredClasses(PersistenceModule.class));
		System.out.println(result.toString(true));
		return result;
	}

	static class TestBean {
		@Inject
		EntityManager manager;
	}

	@Inject
	TestBean bean;

	@Inject
	PersistenceUnitTokenManager contextManager;

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
