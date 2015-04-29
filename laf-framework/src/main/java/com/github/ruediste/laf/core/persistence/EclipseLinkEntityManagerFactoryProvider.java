package com.github.ruediste.laf.core.persistence;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.function.Function;

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

import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.standard.DependencyKey;

public class EclipseLinkEntityManagerFactoryProvider implements
		Function<Class<? extends Annotation>, EntityManagerFactory> {

	@Inject
	Injector injector;

	@Inject
	TransactionManager txm;

	@Inject
	Logger log;

	@Inject
	TransactionProperties txp;

	@Inject
	TransactionIntegrationInfo integrationInfo;

	@Override
	public EntityManagerFactory apply(Class<? extends Annotation> qualifier) {
		DependencyKey<DataSource> key = DependencyKey.of(DataSource.class);
		DataSource dataSource = injector.getInstance(qualifier == null ? key
				: key.withAnnotations(qualifier));

		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setBeanClassLoader(getClass().getClassLoader());

		bean.setPersistenceUnitName("sampleApp");
		bean.setJtaDataSource(dataSource);
		bean.setPersistenceProviderClass(PersistenceProvider.class);

		HashMap<String, Object> props = new HashMap<>();
		props.put(PersistenceUnitProperties.LOGGING_LEVEL,
				SessionLog.FINE_LABEL);
		props.put(PersistenceUnitProperties.WEAVING, "false");
		props.put(PersistenceUnitProperties.TARGET_SERVER, integrationInfo
				.getEclipseLinkExternalTransactionController().getName());

		bean.setJpaPropertyMap(props);

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
}
