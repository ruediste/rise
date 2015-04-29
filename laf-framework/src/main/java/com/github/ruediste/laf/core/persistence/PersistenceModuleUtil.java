package com.github.ruediste.laf.core.persistence;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

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
				Class<?> qualifier);
	}

	/**
	 * @param entityManagerFactoryProvider
	 *            function taking the qualifier and creating an
	 *            {@link EntityManagerFactory}. Will be injected using the
	 *            permanent injector
	 */
	public static void bindDataSource(
			Binder binder,
			Class<? extends Annotation> qualifier,
			Function<Class<? extends Annotation>, EntityManagerFactory> entityManagerFactoryProvider,
			DataSourceFactory dataSourceFactory) {
		DataBaseLink link = new DataBaseLink() {

			DataSource dataSource;

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
				return entityManagerFactoryProvider.apply(qualifier);
			}

			@Inject
			IsolationLevelDataSourceRouter router;

			@Override
			public void initializeDataSource() {
				router.setDataSource(IsolationLevel.SERIALIZABLE,
						dataSourceFactory.createDataSource(
								IsolationLevel.SERIALIZABLE, qualifier));
				router.setDataSource(IsolationLevel.REPEATABLE_READ,
						dataSourceFactory.createDataSource(
								IsolationLevel.REPEATABLE_READ, qualifier));
				dataSource = router;
			}

			@Inject
			DataBaseLinkRegistry registry;

			@PostConstruct
			void register() {
				// register this link
				registry.getLinks().add(this);
			}
		};

		binder.requestInjection(link);
		binder.requestInjection(entityManagerFactoryProvider);

		binder.bind(DataSource.class).annotatedWith(qualifier)
				.toProvider(() -> link.getDataSource());
	}

}
