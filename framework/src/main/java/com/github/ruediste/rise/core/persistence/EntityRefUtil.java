package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Annotation;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.core.persistence.em.PersisteUnitRegistry;

@Singleton
public class EntityRefUtil {

    @Inject
    PersisteUnitRegistry registry;

    @Inject
    EntityManagerHolder holder;

    @SuppressWarnings("unchecked")
    public <T> EntityRef<T> toEntityRef(T entity) {
        Entry<Class<? extends Annotation>, EntityManager> emEntry = holder.getEmEntry(entity).get();
        Class<? extends Annotation> emQualifier = emEntry.getKey();
        EntityRef<T> ref = new EntityRef<>();
        ref.emQualifier = emQualifier;
        ref.entityClass = (Class<T>) entity.getClass();
        ref.key = emEntry.getValue().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        return ref;
    }

    public <T> T load(EntityRef<T> ref) {
        return holder.getEntityManager(ref.emQualifier).find(ref.entityClass, ref.key);
    }
}
