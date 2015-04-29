package com.github.ruediste.laf.core.persistence;

import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Provides;

public class PersistenceDynamicModule extends AbstractModule {

	private Injector permanentInjector;

	public PersistenceDynamicModule(Injector permanentInjector) {
		this.permanentInjector = permanentInjector;
	}

	@Override
	protected void configure() throws Exception {
		DataBaseLinkRegistry registry = permanentInjector
				.getInstance(DataBaseLinkRegistry.class);
		for (DataBaseLink link : registry.getLinks()) {
			// bind data source
			bind(DataSource.class).annotatedWith(link.getQualifier())
					.toProvider(() -> link.getDataSource()).in(Singleton.class);

			// create EMF and bind
			EntityManagerFactory emf = link.createEntityManagerFactory();
			bind(EntityManagerFactory.class).annotatedWith(link.getQualifier())
					.toProvider(() -> emf);
		}
	}

	@Provides
	TransactionManager transactionManager() {
		return permanentInjector.getInstance(TransactionManager.class);
	}
}
