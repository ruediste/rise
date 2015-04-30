package com.github.ruediste.laf.core.persistence;

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

public class EclipseLinkEntityManagerFactoryProvider implements
		EntityManagerFactoryProvider {

	@Inject
	TransactionManager txm;

	@Inject
	Logger log;

	@Inject
	TransactionIntegrationInfo integrationInfo;

	private String persistenceUnitName;

	public EclipseLinkEntityManagerFactoryProvider(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
	}

	@Override
	public EntityManagerFactory createEntityManagerFactory(
			Class<? extends Annotation> qualifier, DataSource dataSource) {

		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setBeanClassLoader(getClass().getClassLoader());

		bean.setPersistenceUnitName(persistenceUnitName);
		bean.setJtaDataSource(dataSource);
		bean.setPersistenceProviderClass(PersistenceProvider.class);

		HashMap<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.LOGGING_LEVEL, getLogLevel());
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_SERVER, integrationInfo
				.getEclipseLinkExternalTransactionController().getName());
		customizeProperties(props);
		bean.setJpaPropertyMap(props);

		customizeFactoryBean(bean);

		try {
			txm.begin();
			bean.afterPropertiesSet();
			txm.commit();
		} catch (Exception e) {
			throw new RuntimeException(
					"Error while creating EntityManagerFactory for "
							+ qualifier, e);
		} finally {
			try {
				if (txm.getStatus() != Status.STATUS_NO_TRANSACTION)
					txm.rollback();
			} catch (Exception e) {
				log.error("Error during rollback, continuing", e);
			}

		}
		return bean.getObject();

	}

	/**
	 * Hook to customize the factory bean
	 */
	private void customizeFactoryBean(
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
}
