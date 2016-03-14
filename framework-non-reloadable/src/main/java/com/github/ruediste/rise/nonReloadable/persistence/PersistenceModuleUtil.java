package com.github.ruediste.rise.nonReloadable.persistence;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

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
        DataSource createDataSource(IsolationLevel isolationLevel, Class<?> qualifier,
                Consumer<Closeable> closeableRegistrar);
    }

    /**
     * @param persistenceUnitManager
     *            Manages a persistence unit. Will be injected using the
     *            permanent injector
     * @param dataSourceFactory
     *            factory for data sources with different isolation levels. Will
     *            be injected using the permanent injector
     */
    public static void bindDataSource(Binder binder, Class<? extends Annotation> qualifier,
            PersistenceUnitManager persistenceUnitManager, DataSourceFactory dataSourceFactory) {
        DataBaseLink link = new DataBaseLink(qualifier, dataSourceFactory, persistenceUnitManager);

        binder.requestInjection(link);

        binder.bind(DataSource.class).annotatedWith(qualifier)
                .toProvider(() -> link.getDataSourceManager().getDataSource());
    }
}
