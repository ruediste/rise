package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Annotation;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * A database link consists of a set of JTA data sources for the different
 * isolation levels, bundled via an {@link IsolationLevelDataSourceRouter} and a
 * factory for associated {@link EntityManagerFactory} ies.
 * 
 * <p>
 * The data source is permanent, while the {@link EntityManagerFactory} gets
 * recreated whenever the dynamic space is reloaded.
 * </p>
 */
public interface DataBaseLink {

	/**
	 * Get the qualifier of this link
	 */
	Class<? extends Annotation> getQualifier();

	/**
	 * Return the data source for this link
	 */
	DataSource getDataSource();

	/**
	 * Create a new {@link EntityManagerFactory} for this link.
	 */
	EntityManagerFactory createEntityManagerFactory();

	/**
	 * Create the {@link DataSource} of this link. Before calling this method,
	 * {@link #getDataSource()} will return null.
	 */
	void initializeDataSource();
}
