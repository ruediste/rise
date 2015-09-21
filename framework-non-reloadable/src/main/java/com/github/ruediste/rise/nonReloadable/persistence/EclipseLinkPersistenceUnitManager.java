package com.github.ruediste.rise.nonReloadable.persistence;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Status;
import javax.transaction.TransactionManager;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.logging.SessionLog;
import org.slf4j.Logger;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.github.ruediste.rise.nonReloadable.front.StartupTimeLogger;
import com.google.common.base.Stopwatch;

/**
 * Manages a persistence unit using EclipseLink
 */
public class EclipseLinkPersistenceUnitManager
        implements PersistenceUnitManager {

    @Inject
    TransactionManager txm;

    @Inject
    Logger log;

    @Inject
    TransactionIntegrationInfo integrationInfo;

    private String persistenceUnitName;

    private LocalContainerEntityManagerFactoryBean bean;

    private volatile boolean isOpen;

    private Class<? extends Annotation> qualifier;

    private DataSourceManager dataSourceManager;

    public EclipseLinkPersistenceUnitManager(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    @Override
    synchronized public void dropAndCreateSchema() {
        Stopwatch watch = Stopwatch.createStarted();
        log.info("Dropping and creating schema of " + qualifierName());
        // we got to close the link and reopen it again with schema generation
        // enabled
        close();
        checkOpen(props -> {
            props.put(
                    PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION,
                    PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
            customizeSchemaGenerationProperties(props);
        });
        close();
        StartupTimeLogger.stopAndLog(
                "Dropping and creatin schema of " + qualifierName(), watch);
        log.info("Dropping and creating schema of " + qualifierName()
                + " completed. Time: " + watch);
    }

    private String qualifierName() {
        return qualifier == null ? "<default>" : qualifier.getSimpleName();
    }

    @Override
    synchronized public EntityManagerFactory getEntityManagerFactory() {
        checkOpen();
        return bean.getObject();
    }

    private void checkOpen() {
        checkOpen(x -> {
        });
    }

    /**
     * open this manager if it is currently closed
     * 
     * @return
     */
    synchronized private HashMap<String, Object> checkOpen(
            Consumer<Map<String, Object>> propertiesCustomizer) {
        // initialize once only
        if (isOpen)
            return null;
        isOpen = true;

        Stopwatch watch = Stopwatch.createStarted();
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        log.info("Creating EntityManagerFactory for " + qualifierName());
        bean = new LocalContainerEntityManagerFactoryBean();

        bean.setBeanClassLoader(classLoader);

        bean.setPersistenceUnitName(persistenceUnitName);
        bean.setJtaDataSource(dataSourceManager.getDataSource());
        bean.setPersistenceProviderClass(PersistenceProvider.class);

        HashMap<String, Object> properties = createProperties(
                propertiesCustomizer);
        bean.setJpaPropertyMap(properties);

        customizeFactoryBean(bean);

        boolean manageTx = false;
        try {
            manageTx = txm.getTransaction() == null;
            if (manageTx)
                txm.begin();
            bean.afterPropertiesSet();

            if (manageTx)
                txm.commit();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while creating EntityManagerFactory for "
                            + qualifierName(),
                    e);
        } finally {
            if (manageTx)
                try {
                    if (txm.getStatus() != Status.STATUS_NO_TRANSACTION)
                        txm.rollback();
                } catch (Exception e) {
                    log.error("Error during rollback, continuing", e);
                }

        }
        log.debug("initialized provider for " + qualifier);
        StartupTimeLogger
                .stopAndLog("Eclipselink Startup of " + qualifierName(), watch);
        return properties;
    }

    protected HashMap<String, Object> createProperties(
            Consumer<Map<String, Object>> propertiesCustomizer) {
        HashMap<String, Object> props = new HashMap<>();
        props.put(PersistenceUnitProperties.CLASSLOADER,
                Thread.currentThread().getContextClassLoader());
        props.put(PersistenceUnitProperties.LOGGING_LEVEL, getLogLevel());
        props.put(PersistenceUnitProperties.WEAVING, "false");
        props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "JTA");
        props.put(PersistenceUnitProperties.TARGET_SERVER, integrationInfo
                .getEclipseLinkExternalTransactionController().getName());
        props.put(PersistenceUnitProperties.SESSION_NAME, sessionName());
        customizeProperties(props);
        propertiesCustomizer.accept(props);
        return props;
    }

    protected String sessionName() {
        return qualifier == null ? "--" : qualifier.getName();
    }

    /**
     * Hook to customize the factory bean
     */
    protected void customizeFactoryBean(
            LocalContainerEntityManagerFactoryBean bean) {
    }

    /**
     * Hook to set the loglevel to be used
     */
    protected String getLogLevel() {
        return SessionLog.WARNING_LABEL;
    }

    /**
     * Hook to modify the properties used to create the
     * {@link EntityManagerFactory}
     */
    protected void customizeProperties(HashMap<String, Object> props) {

    }

    /**
     * Hook to modify the properties used to generate the database schema
     */
    protected void customizeSchemaGenerationProperties(
            Map<String, Object> props) {

    }

    @Override
    synchronized public void close() {
        if (isOpen) {
            isOpen = false;
            EntityManagerSetupImpl entityManagerSetupImpl = org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider
                    .getEntityManagerSetupImpl(sessionName());
            entityManagerSetupImpl.undeploy();
            bean = null;
        }
    }

    @Override
    public void initialize(Class<? extends Annotation> qualifier,
            DataSourceManager dataSourceManager) {
        this.qualifier = qualifier;
        this.dataSourceManager = dataSourceManager;

    }

}
