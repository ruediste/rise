package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Annotation;

import javax.persistence.metamodel.ManagedType;

import com.google.common.base.Objects;

/**
 * Uniquely identifies a persistent type by a persistence unit and a java class
 */
public class PersistentType {

    private final Class<? extends Annotation> emQualifier;
    private final Class<?> entityClass;
    private final ManagedType<?> type;

    public PersistentType(Class<? extends Annotation> emQualifier, Class<?> entityClass, ManagedType<?> type) {
        super();
        this.emQualifier = emQualifier;
        this.entityClass = entityClass;
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(emQualifier, entityClass, getType());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof PersistentType))
            return false;
        PersistentType other = (PersistentType) obj;
        return Objects.equal(emQualifier, other.emQualifier) && Objects.equal(entityClass, other.entityClass)
                && Objects.equal(getType(), other.getType());
    }

    public Class<? extends Annotation> getEmQualifier() {
        return emQualifier;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public ManagedType<?> getType() {
        return type;
    }

}
