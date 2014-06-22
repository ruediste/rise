package laf.component.reqestProcessing;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.RequestProcessor;
import laf.test.DeploymentProvider;
import laf.test.TestEntity;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PersistenceInitialRequestProcessorTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive result = DeploymentProvider
				.getPersistence()
				.addClasses(
						Modules.getAllRequiredClasses(ComponentRequestProcessingModule.class));
		System.out.println(result.toString(true));
		return result;
	}

	@Inject
	PersistenceInitialRequestProcessor processor;

	@Inject
	Instance<EntityManager> entityManagerInstance;

	@Stateful
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public static class Helper {
		@PersistenceContext
		EntityManager em;

		private TestEntity entity;

		public void create() {
			entity = new TestEntity();
			entity.setValue("foo");
			em.persist(entity);
		}

		public void delete() {
			TestEntity e2 = em.merge(entity);
			em.remove(e2);
		}

		public void check(String expected) {
			TestEntity e2 = em.merge(entity);
			em.refresh(e2);
			Assert.assertEquals(expected, e2.getValue());
		}

		public TestEntity getEntity() {
			return entity;
		}

		public void setEntity(TestEntity entity) {
			this.entity = entity;
		}
	}

	@Inject
	Helper helper;

	@Before
	public void before() {
		helper.create();
	}

	@After
	public void after() {
		helper.delete();
	}

	@Test
	public void testSimple() {

		helper.check("foo");

		processor.initialize(new RequestProcessor() {

			@Override
			public ActionResult process(ActionPath<ParameterValueProvider> path) {
				EntityManager em = entityManagerInstance.get();
				Assert.assertTrue(em.isOpen());
				TestEntity en = em.find(TestEntity.class, helper.getEntity()
						.getId());
				en.setValue("bar");
				return null;
			}
		});

		processor.process(null);

		helper.check("bar");
	}
}
