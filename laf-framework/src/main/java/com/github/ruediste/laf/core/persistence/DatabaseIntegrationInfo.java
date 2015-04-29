package com.github.ruediste.laf.core.persistence;

import javax.sql.XADataSource;

import com.github.ruediste.laf.core.persistence.PersistenceModuleUtil.DataSourceFactory;

/**
 * Information used by {@link DataSourceFactory}ies to connect to a specific
 * type of data base
 */
public interface DatabaseIntegrationInfo {
	Class<? extends XADataSource> getDataSourceClass();
}