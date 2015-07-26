package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;

import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.core.persistence.em.PersisteUnitRegistry;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.google.common.reflect.TypeToken;

public class RisePersistenceUtil {
    @Inject
    PersisteUnitRegistry registry;

    @Inject
    DataBaseLinkRegistry dbLinkRegistry;

    @Inject
    EntityManagerHolder holder;

    /**
     * Get the {@link ManagedType} or null if the class is not part of the
     * persistence unit.
     */
    public ManagedType<?> getManagedType(Class<? extends Annotation> qualifier,
            Type type) {
        return getManagedType(qualifier, TypeToken.of(type).getRawType());
    }

    /**
     * Get the {@link ManagedType} or null if the class is not part of the
     * persistence unit.
     */
    public ManagedType<?> getManagedType(Class<? extends Annotation> qualifier,
            Class<?> cls) {
        ManagedType<?> managedType = getManagedTypeMap(qualifier).get()
                .get(cls);
        return managedType;
    }

    /**
     * Get the managed types for the given persistence unit. If the map is not
     * yet loaded, load it.
     */
    public Optional<Map<Class<?>, ManagedType<?>>> getManagedTypeMap(
            Class<? extends Annotation> qualifier) {
        return registry.getManagedTypeMap(qualifier);
    }

    /**
     * There is a number associated with each persistence qualifier. Return the
     * qualifier by it's number
     */
    public Class<? extends Annotation> getQualifierByNr(int nr) {
        return dbLinkRegistry.getQualifierByNr(nr);
    }

    /**
     * There is a number associated with each persistence qualifier. Return the
     * number of a qualifier
     */
    public int getQualifierNr(Class<? extends Annotation> qualifier) {
        return dbLinkRegistry.getQualifierNr(qualifier);
    }

    public Class<?> getIdType(ManagedType<?> managedType) {
        return ((IdentifiableType<?>) managedType).getIdType().getJavaType();
    }

    public Class<?> getIdType(Class<? extends Annotation> qualifier,
            Class<?> cls) {
        return getIdType(getManagedType(qualifier, cls));
    }

    public Object getIdentifier(Object entity) {
        return getIdentifier(holder.getEmEntry(entity).get().getValue(), entity);
    }

    /**
     * Return the identifier of an entity. If the entity manager is not yet
     * known, use {@link #getIdentifier(Object)}
     */
    public Object getIdentifier(EntityManager entityManager, Object entity) {
        return entityManager.getEntityManagerFactory().getPersistenceUnitUtil()
                .getIdentifier(entity);
    }

    /**
     * Return the identifier of an entity. If the entity manager is not yet
     * known, use {@link #getIdentifier(Object)}
     */
    public Object getIdentifier(Class<? extends Annotation> qualifier,
            Object entity) {
        return getIdentifier(holder.getEntityManager(qualifier), entity);
    }

    public Class<? extends Annotation> getEmQualifier(Object entity) {
        return getEmEntry(entity).get().getKey();
    }

    public Optional<Entry<Class<? extends Annotation>, EntityManager>> getEmEntry(
            Object entity) {
        return holder.getEmEntry(entity);
    }

    public EntityManager getEntityManager(Class<? extends Annotation> qualifier) {
        return holder.getEntityManager(qualifier);
    }
}
