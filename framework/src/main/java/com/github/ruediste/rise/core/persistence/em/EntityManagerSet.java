package com.github.ruediste.rise.core.persistence.em;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;

import org.slf4j.Logger;

public class EntityManagerSet {
    @Inject
    Logger log;

    @Inject
    PersisteUnitRegistry registry;

    private HashMap<Class<? extends Annotation>, EntityManager> managers = new HashMap<>();

    public EntityManager getOrCreateEntityManager(Class<? extends Annotation> qualifier) {
        return managers.computeIfAbsent(qualifier, q -> {
            EntityManagerFactory unit = registry.getUnit(qualifier).get();
            log.debug("Creating EntityManager from " + unit);
            return unit.createEntityManager();
        });
    }

    public Iterable<EntityManager> getManagers() {
        return managers.values();
    }

    public Collection<Entry<Class<? extends Annotation>, EntityManager>> getManagerEntries() {
        return managers.entrySet();
    }

    public Optional<Entry<Class<? extends Annotation>, EntityManager>> getEmEntry(Object entity) {
        for (Entry<Class<? extends Annotation>, EntityManager> entry : getManagerEntries()) {
            Map<Class<?>, ManagedType<?>> managedTypeMap = registry.getManagedTypeMap(entry.getKey()).get();
            ManagedType<?> managedType = managedTypeMap.get(entity.getClass());
            if (managedType != null && entry.getValue().contains(entity))
                return Optional.of(entry);

        }
        return Optional.empty();

    }

}