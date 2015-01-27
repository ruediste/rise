package com.github.ruediste.laf.component.core;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import laf.test.DeploymentProvider;
import laf.test.TestEntity;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.ruediste.laf.component.core.*;
import com.github.ruediste.laf.component.core.pageScope.PageScopeManager;
import com.github.ruediste.laf.core.persistence.PersistenceUnitTokenManager;

@RunWith(Arquillian.class)
public class PersistenceInitialRequestHandlerTest {
	@Deployment
	public static WebArchive getDeployment() {
		WebArchive result = DeploymentProvider.getPersistence().addClasses(
				Modules.getAllRequiredClasses(ComponentCoreModule.class));
		return result;
	}

	@Inject
	PageScopeManager pageScopeManager;

	@Inject
	PersistenceInitialRequestHandler handler;

	@Inject
	EntityManager em;

	@Inject
	PageScopedPersistenceHolder holder;

	@Inject
	PersistenceUnitTokenManager persitenceManager;

	@Test
	public void checkPageScopedHolderIsUsed() {

		TestEntity entity = new TestEntity();

		pageScopeManager.enterNew();

		handler.setDelegate(x -> {
			em.persist(entity);
			return null;
		});
		handler.handle(null);

		persitenceManager.withPersistenceHolder(holder, () -> {
			assertTrue(em.contains(entity));
		});

		pageScopeManager.leave();
	}
}
