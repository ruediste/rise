package com.github.ruediste.rise.core.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.core.CoreDynamicModule;
import com.github.ruediste.rise.core.front.LoggerModule;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class EntityManagerTest {

	int dbCount = 0;

	@Before
	public void before() {
		Injector permanentInjector = Salta.createInjector(new AbstractModule() {

			@Override
			protected void configure() throws Exception {
				PersistenceModuleUtil.bindDataSource(binder(), null,
						new EclipseLinkEntityManagerFactoryProvider(
								"frameworkTest"),
						new BitronixDataSourceFactory(
								new H2DatabaseIntegrationInfo()) {

							@Override
							protected void initializeProperties(Properties props) {
								props.setProperty("URL", "jdbc:h2:mem:test"
										+ (dbCount++)
										+ ";DB_CLOSE_DELAY=-1;MVCC=false");
								props.setProperty("user", "sa");
								props.setProperty("password", "sa");
							}
						});
			}
		}, new BitronixModule(), new LoggerModule());

		permanentInjector.getInstance(DataBaseLinkRegistry.class)
				.initializeDataSources();
		Salta.createInjector(new CoreDynamicModule(permanentInjector),
				new LoggerModule()).injectMembers(this);
	}

	@Inject
	TransactionManager txm;

	@Inject
	EntityManager em;

	@Inject
	EntityManagerHolder holder;

	@Test
	public void testSeparateTx() throws Exception {
		long id;
		{
			txm.begin();
			holder.setNewEntityManagerSet();
			TestEntity entity = new TestEntity();
			entity.setValue("Hello");
			em.persist(entity);
			em.flush();
			txm.commit();
			id = entity.getId();
		}
		{
			txm.begin();
			holder.setNewEntityManagerSet();
			TestEntity entity = em.find(TestEntity.class, id);
			assertNotNull(entity);
			assertEquals("Hello", entity.getValue());
			txm.commit();
		}
	}

	@Test
	public void testKeepOpen() throws Exception {
		// create and persist
		TestEntity entity;
		{
			txm.begin();
			holder.setNewEntityManagerSet();
			assertTrue(em.isJoinedToTransaction());
			entity = new TestEntity();
			entity.setValue("Hello");
			em.persist(entity);
			em.flush();
			txm.commit();
		}

		assertTrue(em.isOpen());
		assertFalse(em.isJoinedToTransaction());
		assertTrue(em.contains(entity));

		// without tx, but EM open
		{
			txm.begin();
			assertFalse(em.isJoinedToTransaction());
			entity.setValue("Hello1");

			// does not close the em
			txm.rollback();
		}

		assertTrue(em.isOpen());
		assertFalse(em.isJoinedToTransaction());
		assertTrue(em.contains(entity));

		// finally in tx again
		{
			txm.begin();
			holder.joinTransaction();
			assertTrue(em.isJoinedToTransaction());
			assertTrue(em.contains(entity));
			txm.rollback();

			// entity becomes detached by rollback
			assertFalse(em.contains(entity));
		}

		assertTrue(em.isOpen());
		assertFalse(em.isJoinedToTransaction());

	}
}
