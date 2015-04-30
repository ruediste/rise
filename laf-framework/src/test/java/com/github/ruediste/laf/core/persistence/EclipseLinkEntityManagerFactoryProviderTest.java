package com.github.ruediste.laf.core.persistence;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.TransactionManager;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.laf.core.front.LoggerModule;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class EclipseLinkEntityManagerFactoryProviderTest {

	@Before
	public void before() {
		Injector permanentInjector = Salta.createInjector(new AbstractModule() {

			@Override
			protected void configure() throws Exception {
				PersistenceModuleUtil
						.bindDataSource(binder(), null,
								new EclipseLinkEntityManagerFactoryProvider(
										"sampleApp"),
								new BitronixDataSourceFactory(
										new H2DatabaseIntegrationInfo()) {

									@Override
									protected void initializeProperties(
											Properties props) {
										props.setProperty("URL",
												"jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MVCC=false");
										props.setProperty("user", "sa");
										props.setProperty("password", "sa");
									}
								});
			}
		}, new BitronixModule(), new LoggerModule());

		permanentInjector.getInstance(DataBaseLinkRegistry.class)
				.initializeDataSources();
		Salta.createInjector(new PersistenceDynamicModule(permanentInjector))
				.injectMembers(this);
	}

	@Inject
	TransactionManager txm;

	@Inject
	EntityManagerFactory emf;

	@Test
	public void test() throws Exception {
		{
			txm.begin();
			EntityManager em = emf.createEntityManager();
			TestEntity entity = new TestEntity();
			entity.setValue("Hello");
			em.persist(entity);
			em.flush();
			txm.commit();
		}
		{
			txm.begin();
			EntityManager em = emf.createEntityManager();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TestEntity> q = cb.createQuery(TestEntity.class);
			Root<TestEntity> root = q.from(TestEntity.class);
			q.select(root);
			List<TestEntity> resultList = em.createQuery(q).getResultList();
			assertEquals(1, resultList.size());
			assertEquals("Hello", resultList.get(0).getValue());
			txm.commit();
		}

	}
}
