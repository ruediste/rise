package com.github.ruediste.rise.nonReloadable.persistence;

import javax.sql.XADataSource;

import com.github.ruediste.rise.nonReloadable.persistence.PersistenceModuleUtil.DataSourceFactory;

/**
 * Information used by {@link DataSourceFactory}ies to connect to a specific
 * type of data base
 */
public interface DatabaseIntegrationInfo {
	Class<? extends XADataSource> getDataSourceClass();
}