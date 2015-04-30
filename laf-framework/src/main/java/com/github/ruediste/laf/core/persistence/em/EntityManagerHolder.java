package com.github.ruediste.laf.core.persistence.em;

import java.lang.annotation.Annotation;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

@Singleton
public class EntityManagerHolder {

	@Inject
	Provider<EntityManagerSet> setProvider;

	private final ThreadLocal<EntityManagerSet> currentSet = new ThreadLocal<>();

	public EntityManager getEntityManager(Class<? extends Annotation> qualifier) {
		return getOrCreateCurrentEntityManagerSet().getOrCreateEntityManager(
				qualifier);
	}

	public void setCurrentEntityManagerSet(EntityManagerSet set) {
		currentSet.set(set);
	}

	public EntityManagerSet getCurrentEntityManagerSet() {
		return currentSet.get();
	}

	public void removeCurrentSet() {
		currentSet.remove();
	}

	public EntityManagerSet getOrCreateCurrentEntityManagerSet() {
		EntityManagerSet result = currentSet.get();
		if (result == null) {
			result = setProvider.get();
			currentSet.set(result);
		}
		return result;
	}
}
