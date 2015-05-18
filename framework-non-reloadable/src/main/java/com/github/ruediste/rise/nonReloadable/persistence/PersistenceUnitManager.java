package com.github.ruediste.rise.nonReloadable.persistence;

import java.lang.annotation.Annotation;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Manages a persistence unit, represented by an {@link EntityManagerFactory}.
 * One insetance is created per persistence unit. The methods of this class are
 * thread safe.
 * 
 * @see PersistenceModuleUtil#bindDataSource
 */
public interface PersistenceUnitManager {

	void initialize(Class<? extends Annotation> qualifier, DataSource dataSource);

	void generateSchema();

	EntityManagerFactory getEntityManagerFactory();

	void close();
}
