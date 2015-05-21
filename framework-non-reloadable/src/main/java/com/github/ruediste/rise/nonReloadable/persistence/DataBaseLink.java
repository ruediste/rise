package com.github.ruediste.rise.nonReloadable.persistence;

import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;

import com.github.ruediste.rise.nonReloadable.persistence.PersistenceModuleUtil.DataSourceFactory;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * A database link consists of a set of JTA data sources for the different
 * isolation levels, bundled via an {@link IsolationLevelDataSourceRouter} and a
 * factory for associated {@link EntityManagerFactory} ies.
 * 
 * <p>
 * The data source is not restartable, while the {@link EntityManagerFactory}
 * gets recreated whenever the application restarts.
 * </p>
 */
public class DataBaseLink {

	@Inject
	DataBaseLinkRegistry registry;

	@Inject
	Logger log;

	private final PersistenceUnitManager persistenceUnitManager;
	private final DataSourceManager dataSourceManager;

	private final Class<? extends Annotation> qualifier;

	public DataBaseLink(Class<? extends Annotation> qualifier,
			DataSourceFactory dataSourceFactory,
			PersistenceUnitManager persistenceUnitManager) {
		super();
		this.qualifier = qualifier;
		this.persistenceUnitManager = persistenceUnitManager;
		this.dataSourceManager = new DataSourceManager(qualifier,
				dataSourceFactory);
		persistenceUnitManager.initialize(qualifier, dataSourceManager);

	}

	@PostConstruct
	void setup(Injector injector) {
		// register this link
		registry.addLink(this);

		injector.injectMembers(dataSourceManager);
		injector.injectMembers(persistenceUnitManager);
	}

	public DataSourceManager getDataSourceManager() {
		return dataSourceManager;
	}

	public PersistenceUnitManager getPersistenceUnitManager() {
		return persistenceUnitManager;
	}

	public Class<? extends Annotation> getQualifier() {
		return qualifier;
	}

	public void close() {
		getPersistenceUnitManager().close();
		getDataSourceManager().close();
	}

	public void runSchemaMigration() {
		log.info("Running DB schema migration ...");

		// Not Yet implemented. Run Flyway scripts
		log.warn("schema migration not yet implemented");

		Flyway flyway = new Flyway();
		flyway.setDataSource(dataSourceManager.getDataSource());
		flyway.setLocations("db/migration/" + qualifier == null ? "default"
				: qualifier.getSimpleName());
		flyway.migrate();

		log.info("DB schema migration complete");
	}
}
