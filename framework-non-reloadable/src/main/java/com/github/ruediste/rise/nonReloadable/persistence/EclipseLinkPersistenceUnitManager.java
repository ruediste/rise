package com.github.ruediste.rise.nonReloadable.persistence;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.TransactionManager;

import org.eclipse.persistence.config.PersistenceUnitProperties;
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

	private boolean initialized;

	public EclipseLinkPersistenceUnitManager(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
	}

	@Override
	synchronized public void generateSchema() {
		checkInitialized();
		HashMap<String, Object> props = new HashMap<>();
		customizeSchemaGenerationProperties(props);
		bean.getPersistenceProvider().generateSchema(
				bean.getPersistenceUnitInfo(), props);
	}

	@Override
	synchronized public EntityManagerFactory getEntityManagerFactory() {
		checkInitialized();
		return bean.getObject();
	}

	private void checkInitialized() {
		synchronized (this) {
			if (!initialized)
				throw new RuntimeException("manager not initialized");
		}
	}

	/**
	 * initialize this provider if not yet initialized
	 */
	@Override
	synchronized public void initialize(Class<? extends Annotation> qualifier,
			DataSource dataSource) {
		// initialize once only
		if (initialized)
			throw new RuntimeException("manager already initialized");
		initialized = true;

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		log.info("Creating EntityManagerFactory for " + qualifier);
		bean = new LocalContainerEntityManagerFactoryBean();

		bean.setBeanClassLoader(classLoader);

		bean.setPersistenceUnitName(persistenceUnitName);
		bean.setJtaDataSource(dataSource);
		bean.setPersistenceProviderClass(PersistenceProvider.class);

		HashMap<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.CLASSLOADER, classLoader);
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, getLogLevel());
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "JTA");
		props.put(PersistenceUnitProperties.TARGET_SERVER, integrationInfo
				.getEclipseLinkExternalTransactionController().getName());
		customizeProperties(props);
		bean.setJpaPropertyMap(props);

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
		checkInitialized();
		initialized = false;
		org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider
				.getEntityManagerSetupImpl(persistenceUnitName).undeploy();
	}

}
