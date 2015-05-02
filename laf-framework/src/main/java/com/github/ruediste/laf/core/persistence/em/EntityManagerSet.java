package com.github.ruediste.laf.core.persistence.em;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class EntityManagerSet {
	@Inject
	PersisteUnitRegistry registry;

	private HashMap<Class<? extends Annotation>, EntityManager> managers = new HashMap<>();

	public EntityManager getOrCreateEntityManager(
			Class<? extends Annotation> qualifier) {
		return managers.computeIfAbsent(qualifier,
				q -> registry.getUnit(qualifier).get().createEntityManager());
	}

	public Iterable<EntityManager> getManagers() {
		return managers.values();
	}

}