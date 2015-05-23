package com.github.ruediste.rise.nonReloadable.persistence;

import javax.sql.XADataSource;

import org.h2.jdbcx.JdbcDataSource;

public class H2DatabaseIntegrationInfo implements DatabaseIntegrationInfo {

	@Override
	public Class<? extends XADataSource> getDataSourceClass() {
		return JdbcDataSource.class;
	}

}