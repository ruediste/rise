package com.github.ruediste.rise.nonReloadable.persistence;

import java.io.Closeable;
import java.util.Properties;
import java.util.function.Consumer;

import javax.sql.DataSource;

import bitronix.tm.resource.jdbc.PoolingDataSource;

import com.github.ruediste.rise.nonReloadable.persistence.PersistenceModuleUtil.DataSourceFactory;
import com.p6spy.engine.spy.P6DataSource;

public abstract class BitronixDataSourceFactory implements DataSourceFactory {

    private DatabaseIntegrationInfo databaseIntegrationInfo;

    public BitronixDataSourceFactory(
            DatabaseIntegrationInfo databaseIntegrationInfo) {
        this.databaseIntegrationInfo = databaseIntegrationInfo;
    }

    @Override
    public DataSource createDataSource(IsolationLevel isolationLevel,
            Class<?> qualifier, Consumer<Closeable> closeableRegistrar) {
        Properties props = new Properties();
        initializeProperties(props);

        PoolingDataSource btmDataSource = new PoolingDataSource();
        btmDataSource.setUniqueName(
                (qualifier == null ? "" : qualifier.getSimpleName()) + "_"
                        + isolationLevel.name());
        btmDataSource.setClassName(
                databaseIntegrationInfo.getDataSourceClass().getName());
        btmDataSource.setIsolationLevel(isolationLevel.name());
        btmDataSource.setDriverProperties(props);
        btmDataSource.setShareTransactionConnections(true);
        btmDataSource.setMaxPoolSize(10);
        btmDataSource.setAllowLocalTransactions(true);
        btmDataSource.setMinPoolSize(1);
        customizeDataSource(btmDataSource);

        closeableRegistrar.accept(btmDataSource::close);
        return new P6DataSource(btmDataSource);
    }

    /**
     * Hook to customize the created bitronix data source
     */
    protected void customizeDataSource(PoolingDataSource btmDataSource) {

    }

    /**
     * Initialize the properties which are set on the DataSouce connecting to
     * the underlying data base
     */
    protected abstract void initializeProperties(Properties props);

}