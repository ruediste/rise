package com.github.ruediste.rise.core.persistence.em;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;

public class EntityManagerSet {
    @Inject
    Logger log;

    @Inject
    PersisteUnitRegistry registry;

    private HashMap<Class<? extends Annotation>, EntityManager> managers = new HashMap<>();

    public EntityManager getOrCreateEntityManager(
            Class<? extends Annotation> qualifier) {
        return managers.computeIfAbsent(qualifier, q -> {
            EntityManagerFactory unit = registry.getUnit(qualifier).get();
            log.debug("Creating EntityManager from " + unit);
            return unit.createEntityManager();
        });
    }

    public Iterable<EntityManager> getManagers() {
        return managers.values();
    }

}