package com.github.ruediste.rise.core.persistence.em;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

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
		EntityManagerSet set = getCurrentEntityManagerSet();
		if (set == null) {
			throw new RuntimeException("No EntityManagerSet is currently set");
		}
		return set.getOrCreateEntityManager(qualifier);
	}

	public void setCurrentEntityManagerSet(EntityManagerSet set) {
		if (set == null)
			currentSet.remove();
		else
			currentSet.set(set);
	}

	public EntityManagerSet getCurrentEntityManagerSet() {
		return currentSet.get();
	}

	public void removeCurrentSet() {
		currentSet.remove();
	}

	/**
	 * Set a new EntityManagerSet
	 * 
	 * @return the old {@link EntityManagerSet}
	 */
	public EntityManagerSet setNewEntityManagerSet() {
		EntityManagerSet oldSet = currentSet.get();
		currentSet.set(createEntityManagerSet());
		return oldSet;
	}

	public EntityManagerSet createEntityManagerSet() {
		return setProvider.get();
	}

	public void flush() {
		foreachCurrentManager(EntityManager::flush);
	}

	public void closeCurrentEntityManagers() {
		foreachCurrentManager(EntityManager::close);
	}

	public void foreachCurrentManager(Consumer<? super EntityManager> action) {
		getCurrentManagers().forEach(action);
	}

	public void joinTransaction() {
		foreachCurrentManager(EntityManager::joinTransaction);
	}

	public Iterable<EntityManager> getCurrentManagers() {
		return getCurrentEntityManagerSet().getManagers();
	}
}
