package com.github.ruediste.laf.core.persistence.em;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;

import com.github.ruediste.laf.core.Permanent;
import com.github.ruediste.laf.core.persistence.DataBaseLinkRegistry;

@Singleton
public class PersisteUnitRegistry {

	@Inject
	@Permanent
	DataBaseLinkRegistry registry;

	private ConcurrentHashMap<Class<? extends Annotation>, EntityManagerFactory> factories = new ConcurrentHashMap<>();

	private static @interface NullQualifier {
	}

	public synchronized EntityManagerFactory getUnit(
			Class<? extends Annotation> qualifier) {
		return factories.computeIfAbsent(
				qualifier == null ? NullQualifier.class : qualifier,
				q -> registry.getLink(qualifier).createEntityManagerFactory());

	}

}
