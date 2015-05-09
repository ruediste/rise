package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Annotation;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

public interface EntityManagerFactoryProvider {

	EntityManagerFactory createEntityManagerFactory(
			Class<? extends Annotation> qualifier, DataSource dataSource);
}
