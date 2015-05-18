package com.github.ruediste.rise.nonReloadable.persistence;

import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;

import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.binder.Binder;

/**
 * Utility class to register {@link DataBaseLink}s from {@link AbstractModule}s
 */
public class PersistenceModuleUtil {

	/**
	 * Factory for {@link DataSource} with a given {@link IsolationLevel}
	 */
	public interface DataSourceFactory {
		DataSource createDataSource(IsolationLevel isolationLevel,
				Class<?> qualifier, Consumer<Closeable> closeableRegistrar);
	}

	/**
	 * @param persistenceUnitManager
	 *            function taking the qualifier and creating an
	 *            {@link EntityManagerFactory}. Will be injected using the
	 *            permanent injector
	 */
	public static void bindDataSource(Binder binder,
			Class<? extends Annotation> qualifier,
			PersistenceUnitManager persistenceUnitManager,
			DataSourceFactory dataSourceFactory) {
		DataBaseLink link = new DataBaseLink() {
			@Inject
			IsolationLevelDataSourceRouter router;

			@Inject
			DataBaseLinkRegistry registry;

			@Inject
			Logger log;

			DataSource dataSource;

			ArrayList<Closeable> closeables = new ArrayList<>();

			@Override
			public DataSource getDataSource() {
				return dataSource;
			}

			@Override
			public Class<? extends Annotation> getQualifier() {
				return qualifier;
			}

			@Override
			public EntityManagerFactory createEntityManagerFactory() {
				return persistenceUnitManager.createEntityManagerFactory(
						qualifier, dataSource);
			}

			@Override
			public void initializeDataSource() {
				DataSource serializable = dataSourceFactory
						.createDataSource(IsolationLevel.SERIALIZABLE,
								qualifier, closeables::add);
				router.setDataSource(IsolationLevel.SERIALIZABLE, serializable);

				DataSource repeatableRead = dataSourceFactory.createDataSource(
						IsolationLevel.REPEATABLE_READ, qualifier,
						closeables::add);
				router.setDataSource(IsolationLevel.REPEATABLE_READ,
						repeatableRead);

				dataSource = router;
			}

			@PostConstruct
			void register() {
				// register this link
				registry.addLink(this);
			}

			@Override
			public void close() {
				for (Closeable c : closeables) {
					try {
						c.close();
					} catch (IOException e) {
						log.error(
								"Error while closing DB connection, continuing...",
								e);
					}
				}
			}
		};

		binder.requestInjection(link);
		binder.requestInjection(persistenceUnitManager);

		binder.bind(DataSource.class).annotatedWith(qualifier)
				.toProvider(() -> link.getDataSource());
	}
}
