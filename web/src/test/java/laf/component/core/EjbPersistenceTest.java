package laf.component.core;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.*;

import laf.test.DeploymentProvider;
import laf.test.TestEntity;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EjbPersistenceTest {
	@Deployment
	public static WebArchive getDeployment() {
		WebArchive result = DeploymentProvider.getPersistenceNoProducer();
		return result;
	}

	@Stateful
	@TransactionManagement(TransactionManagementType.BEAN)
	public static class PCHolder {

		@PersistenceContext(type = PersistenceContextType.EXTENDED)
		EntityManager em;

		@Resource
		EJBContext context;

		@Inject
		TestService ts;

		public void begin() throws Exception {
			context.getUserTransaction().begin();
		}

		public void rollback() throws Exception {
			context.getUserTransaction().rollback();
		}

		public void commit() throws Exception {
			context.getUserTransaction().commit();
		}

		public void execute(Consumer<EntityManager> func) {
			func.accept(em);
		}

		public void check(TestEntity entity) {
			assertTrue(em.contains(entity));
			ts.execute(em2 -> {

				assertTrue(em2.contains(entity));
			});
		}

		@Remove
		public void remove() {

		}
	}

	public static class TestService implements Serializable {
		@PersistenceContext
		EntityManager em;

		public void execute(Consumer<EntityManager> func) {
			func.accept(em);
		}

	}

	@Inject
	PCHolder pcHolder;

	@Inject
	TestService testService;

	@Test
	public void test() throws Exception {
		TestEntity entity = new TestEntity();
		pcHolder.execute(em -> {
			em.persist(entity);
		});

		pcHolder.begin();
		pcHolder.check(entity);
		// pcHolder.execute(em -> {
		// assertTrue(em.contains(entity));
		// testService.execute(em2 -> {
		// assertTrue(em2.contains(entity));
		// });
		// s});

		entity.setValue("Foo");

		pcHolder.commit();
	}
}
