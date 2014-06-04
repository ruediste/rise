package laf.component.transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import laf.test.ComponentDeploymentProvider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

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

	@Stateful
	@TransactionManagement(TransactionManagementType.CONTAINER)
	public static class StatefulSessionBean implements Serializable {

		@PersistenceContext(type = PersistenceContextType.EXTENDED)
		EntityManager manager;

		@Inject
		TestBean bean;

		@TransactionAttribute(TransactionAttributeType.REQUIRED)
		public void test() {
			TestEntity entity = new TestEntity();
			entity.setValue("foo");
			entity.setId(1);
			manager.persist(entity);
			bean.test("foo");
			entity.setValue("bar");
			bean.test("bar");
		}

		@Remove
		public void remove() {

		}
	}

	@Stateless
	public static class TestBean implements Serializable {
		@PersistenceContext
		EntityManager manager;

		public void test(String expected) {
			TestEntity entity = manager.find(TestEntity.class, 1L);
			assertNotNull(entity);
			assertEquals(expected, entity.getValue());

			assertNotNull(TestEntity_.class);
		}
	}

	@Inject
	StatefulSessionBean sessionBean;

	@Test
	public void simple() {
		sessionBean.test();
	}
}
