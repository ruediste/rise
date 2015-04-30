package com.github.ruediste.laf.core.persistence.em;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class EntityManagerSet {
	@Inject
	PersisteUnitRegistry registry;

	HashMap<Class<? extends Annotation>, EntityManager> managers;

	public EntityManager getOrCreateEntityManager(
			Class<? extends Annotation> qualifier) {
		return managers.computeIfAbsent(qualifier,
				q -> registry.getUnit(qualifier).createEntityManager());
	}
}