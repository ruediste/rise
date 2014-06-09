package laf.component.transaction;

import static org.junit.Assert.*;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import laf.base.Val;
import laf.component.core.PagePersistenceManager;
import laf.test.ComponentDeploymentProvider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

@RunWith(Arquillian.class)
public class TransactionTest {

	@Deployment
	public static WebArchive deployment() {
		WebArchive result = ComponentDeploymentProvider
				.getDefault()
				.addAsResource("test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsResource("test-openejb.xml", "META-INF/openejb.xml")
				.addClasses(TestEntity.class);
		System.out.println(result.toString(true));
		return result;
	}

	@Stateless
	public static class TestBean implements Serializable {
		private static final long serialVersionUID = 1L;

		@Inject
		EntityManager manager;

		public void test(String expected) {
			TestEntity entity = manager.find(TestEntity.class, 1L);
			assertNotNull(entity);
			assertEquals(expected, entity.getValue());

			assertNotNull(TestEntity_.class);
		}
	}

	@Stateless
	public static class Initializer implements Serializable {
		private static final long serialVersionUID = 1L;
		@PersistenceContext
		EntityManager manager;
		private TestEntity entity;

		public void initialize() {
			entity = new TestEntity();
			entity.setValue("parent");
			TestEntity child = new TestEntity();
			child.setValue("child");
			child.setParent(entity);
			entity.getChildren().add(child);
			manager.persist(entity);
		}

		public long getEntityId() {
			return entity.getId();
		}

		public void check(String expected) {
			TestEntity loaded = manager.find(TestEntity.class, getEntityId());
			assertEquals(expected, loaded.getValue());
		}
	}

	@Inject
	PagePersistenceManager sessionBean;

	@Inject
	EntityManager manager;

	@Inject
	Initializer initializer;

	private long entityId;

	@Before
	public void setup() {
		initializer.initialize();
		entityId = initializer.getEntityId();
	}

	@Test
	public void pageLifecycleRollback() {
		final Val<TestEntity> entity = new Val<>();
		// Call initial controller method. Commit Transaction, use extended PC
		sessionBean.initialize(new Runnable() {

			@Override
			public void run() {
				entity.set(manager.find(TestEntity.class, entityId));
				assertEquals("parent", entity.get().getValue());
			}
		});

		// reload page, no transaction, same PC
		sessionBean.withManager(new Runnable() {

			@Override
			public void run() {
				// access children
				TestEntity child = Iterables.getOnlyElement(entity.get()
						.getChildren());
				assertEquals("child", child.getValue());

				// check if child can be loaded from PC
				assertSame(child, manager.find(TestEntity.class, child.getId()));

				child.setValue("modified");
			}
		});

		initializer.check("parent");
		// destroy
		sessionBean.remove();

		initializer.check("parent");
	}

	@Test
	public void pageLifecycleCommit() {

		final Val<TestEntity> val = new Val<>();
		// Call initial controller method. Commit Transaction, use extended PC
		sessionBean.initialize(new Runnable() {

			@Override
			public void run() {
				val.set(manager.find(TestEntity.class, entityId));
			}
		});

		// reload page, no transaction, same PC
		sessionBean.withManager(new Runnable() {

			@Override
			public void run() {
				val.get().setValue("modified");
			}
		});

		initializer.check("parent");

		// reload page, finish with commit, same PC
		sessionBean.commit();

		initializer.check("modified");
	}
}
