package laf.component.reqestProcessing;

import static org.junit.Assert.assertEquals;

import javax.ejb.*;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.component.core.ControllerUtil;
import laf.component.core.impl.ComponentCoreImplModule;
import laf.component.pageScope.PageScopeManager;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.RequestProcessor;
import laf.test.DeploymentProvider;
import laf.test.TestEntity;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PersistenceInPageRequestProcessorTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive result = DeploymentProvider
				.getPersistence()
				.addClasses(
						Modules.getAllRequiredClasses(ComponentRequestProcessingModule.class))
						.addClasses(
								Modules.getAllRequiredClasses(ComponentCoreImplModule.class));
		System.out.println(result.toString(true));
		return result;
	}

	@Inject
	PersistenceInPageRequestProcessor processor;

	@Inject
	Helper helper;

	@Inject
	Instance<EntityManager> entityManagerInstance;

	@Inject
	PageScopeManager pageScopeManager;

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

	@Before
	public void before() {
		helper.create();
		pageScopeManager.enterNew();
	}

	@After
	public void after() {
		helper.delete();
		pageScopeManager.leave();
	}

	@Test
	public void testNoCommit() {

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

		helper.check("foo");
	}

	@Inject
	ControllerUtil util;

	@Test
	public void testCommit() {

		helper.check("foo");

		processor.initialize(new RequestProcessor() {

			@Override
			public ActionResult process(ActionPath<ParameterValueProvider> path) {
				EntityManager em = entityManagerInstance.get();
				Assert.assertTrue(em.isOpen());
				TestEntity en = em.find(TestEntity.class, helper.getEntity()
						.getId());
				en.setValue("bar");
				util.commit();
				return null;
			}
		});

		processor.process(null);

		helper.check("bar");
	}

	@Test
	public void testCommitChecker() {

		helper.check("foo");

		processor.initialize(new RequestProcessor() {

			@Override
			public ActionResult process(ActionPath<ParameterValueProvider> path) {
				final EntityManager em = entityManagerInstance.get();
				Assert.assertTrue(em.isOpen());
				TestEntity en = em.find(TestEntity.class, helper.getEntity()
						.getId());
				en.setValue("bar");
				util.checkAndCommit(new Runnable() {

					@Override
					public void run() {
						TestEntity en2 = em.find(TestEntity.class, helper
								.getEntity().getId());
						assertEquals("foo", en2.getValue());
					}
				});
				return null;
			}
		});

		processor.process(null);

		helper.check("bar");
	}

	@Test
	public void testException() {

		helper.check("foo");

		processor.initialize(new RequestProcessor() {

			@Override
			public ActionResult process(ActionPath<ParameterValueProvider> path) {
				EntityManager em = entityManagerInstance.get();
				TestEntity en = em.find(TestEntity.class, helper.getEntity()
						.getId());
				en.setValue("bar");
				throw new RuntimeException("Test");
			}
		});

		try {
			processor.process(null);
		} catch (RuntimeException e) {
			Assert.assertEquals("Test", e.getMessage());
		}

		helper.check("foo");
	}
}
