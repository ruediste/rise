package com.github.ruediste.rise.nonReloadable.persistence;

import java.lang.annotation.Annotation;

import javax.persistence.EntityManagerFactory;

/**
 * Manages a persistence unit, represented by an {@link EntityManagerFactory}.
 * One insetance is created per persistence unit. The methods of this class are
 * thread safe.
 * 
 * @see PersistenceModuleUtil#bindDataSource
 */
public interface PersistenceUnitManager {

    /**
     * Initialize this manager. Does not cause the {@link EntityManagerFactory}
     * to be created. This happens lazily
     */
    void initialize(Class<? extends Annotation> qualifier,
            DataSourceManager dataSourceManager);

    /**
     * Close this manager. Necessary to release resources.
     */
    void close();

    /**
     * Drop and create the schema for this persistence unit
     */
    void dropAndCreateSchema();

    /**
     * get the {@link EntityManagerFactory} for this manager
     */
    EntityManagerFactory getEntityManagerFactory();

}
