package com.github.ruediste.rise.nonReloadable.persistence;

import java.lang.annotation.Annotation;
import java.util.HashMap;

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

/**
 * Manages a persistence unit using EclipseLink
 */
public class EclipseLinkPersistenceUnitManager implements
		PersistenceUnitManager {

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
		checkOpen();
		HashMap<String, Object> props = createProperties();
		props.put(
				PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION,
				PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
		customizeSchemaGenerationProperties(props);
		bean.getPersistenceProvider().generateSchema(
				bean.getPersistenceUnitInfo(), props);
	}

	@Override
	synchronized public EntityManagerFactory getEntityManagerFactory() {
		checkOpen();
		return bean.getObject();
	}

	/**
	 * open this manager if it is currently closed
	 */
	synchronized private void checkOpen() {
		// initialize once only
		if (isOpen)
			return;
		isOpen = true;

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		log.info("Creating EntityManagerFactory for " + qualifier);
		bean = new LocalContainerEntityManagerFactoryBean();

		bean.setBeanClassLoader(classLoader);

		bean.setPersistenceUnitName(persistenceUnitName);
		bean.setJtaDataSource(dataSourceManager.getDataSource());
		bean.setPersistenceProviderClass(PersistenceProvider.class);

		bean.setJpaPropertyMap(createProperties());

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
							+ qualifier, e);
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
	}

	protected HashMap<String, Object> createProperties() {
		HashMap<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.CLASSLOADER, Thread.currentThread()
				.getContextClassLoader());
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, getLogLevel());
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "JTA");
		props.put(PersistenceUnitProperties.TARGET_SERVER, integrationInfo
				.getEclipseLinkExternalTransactionController().getName());
		props.put(PersistenceUnitProperties.SESSION_NAME, sessionName());
		customizeProperties(props);
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
		return SessionLog.INFO_LABEL;
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
			HashMap<String, Object> props) {

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
