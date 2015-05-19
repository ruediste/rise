package com.github.ruediste.rise.nonReloadable.persistence;

import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.slf4j.Logger;

import com.github.ruediste.rise.nonReloadable.persistence.PersistenceModuleUtil.DataSourceFactory;

public class DataSourceManager {

	@Inject
	Logger log;

	@Inject
	IsolationLevelDataSourceRouter dataSource;

	private final Class<? extends Annotation> qualifier;
	private final DataSourceFactory dataSourceFactory;
	private final ArrayList<Closeable> closeables = new ArrayList<>();
	private boolean isOpen;

	public DataSourceManager(Class<? extends Annotation> qualifier,
			DataSourceFactory dataSourceFactory) {
		super();
		this.qualifier = qualifier;
		this.dataSourceFactory = dataSourceFactory;
	}

	/**
	 * Get the qualifier of this link
	 */
	public Class<? extends Annotation> getQualifier() {
		return qualifier;
	}

	/**
	 * Return the data source for this link
	 */
	synchronized public DataSource getDataSource() {
		checkOpen();
		return dataSource;
	}

	/**
	 * Create the {@link DataSource} of this link.
	 */
	private void checkOpen() {
		if (isOpen)
			return;
		isOpen = true;
		DataSource serializable = dataSourceFactory.createDataSource(
				IsolationLevel.SERIALIZABLE, qualifier, closeables::add);
		dataSource.setDataSource(IsolationLevel.SERIALIZABLE, serializable);

		DataSource repeatableRead = dataSourceFactory.createDataSource(
				IsolationLevel.REPEATABLE_READ, qualifier, closeables::add);
		dataSource
				.setDataSource(IsolationLevel.REPEATABLE_READ, repeatableRead);

	}

	synchronized public void close() {
		if (isOpen) {
			isOpen = false;
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
	}

}
