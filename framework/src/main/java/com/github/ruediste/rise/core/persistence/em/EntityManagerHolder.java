package com.github.ruediste.rise.core.persistence.em;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

@Singleton
public class EntityManagerHolder {

    @Inject
    Logger log;

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

    public void withEntityManagerSet(EntityManagerSet set, Runnable r) {
        withEntityManagerSet(set, () -> {
            r.run();
            return null;
        });
    }

    public <T> T withEntityManagerSet(EntityManagerSet set, Supplier<T> supplier) {
        EntityManagerSet old = getCurrentEntityManagerSet();
        try {
            setCurrentEntityManagerSet(set);
            return supplier.get();
        } finally {
            setCurrentEntityManagerSet(old);
        }
    }

    public void withNewEntityManagerSet(Runnable r) {
        withNewEntityManagerSet(() -> {
            r.run();
            return null;
        });
    }

    public <T> T withNewEntityManagerSet(Supplier<T> supplier) {
        EntityManagerSet old = null;
        try {
            old = setNewEntityManagerSet();
            return supplier.get();
        } finally {
            setCurrentEntityManagerSet(old);
        }
    }

    public EntityManagerSet setCurrentEntityManagerSet(EntityManagerSet set) {
        EntityManagerSet oldSet = getCurrentEntityManagerSet();
        log.trace("Setting entity manager set to {}", set);
        if (set == null)
            currentSet.remove();
        else
            currentSet.set(set);
        return oldSet;
    }

    /**
     * Return the current entity manager set or null if none is present
     */
    public EntityManagerSet getCurrentEntityManagerSet() {
        return currentSet.get();
    }

    public void removeCurrentSet() {
        setCurrentEntityManagerSet(null);
    }

    /**
     * Set a new EntityManagerSet
     * 
     * @return the old {@link EntityManagerSet}
     */
    public EntityManagerSet setNewEntityManagerSet() {
        return setCurrentEntityManagerSet(createEntityManagerSet());
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
