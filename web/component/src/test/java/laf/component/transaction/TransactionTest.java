package laf.component.transaction;

import static org.junit.Assert.assertEquals;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.*;

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
		return ComponentDeploymentProvider.getDefault().addAsWebInfResource(

		TransactionTest.class.getPackage(), "persistence.xml",
				"persistence.xml");
	}

	@Stateful
	@TransactionManagement(TransactionManagementType.CONTAINER)
	public static class StatefulSessionBean {

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
		}
	}

	public static class TestBean {
		@Inject
		EntityManager manager;

		public void test(String expected) {
			TestEntity entity = manager.find(TestEntity.class, 1);
			assertEquals(expected, entity.getClass());
		}
	}

	@Inject
	StatefulSessionBean sessionBean;

	@Test
	public void simple() {
		sessionBean.test();
	}
}
