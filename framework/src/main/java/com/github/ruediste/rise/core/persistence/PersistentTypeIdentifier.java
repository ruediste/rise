package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Annotation;

import com.google.common.base.Objects;

/**
 * Uniquely identifies a persistent type by a persistence unit and a java class
 */
public class PersistentTypeIdentifier {

    private final Class<? extends Annotation> emQualifier;
    private final Class<?> type;

    public PersistentTypeIdentifier(Class<? extends Annotation> emQualifier,
            Class<?> type) {
        super();
        this.emQualifier = emQualifier;
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getEmQualifier(), getType());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof PersistentTypeIdentifier))
            return false;
        PersistentTypeIdentifier other = (PersistentTypeIdentifier) obj;
        return Objects.equal(getEmQualifier(), other.getEmQualifier())
                && Objects.equal(getType(), other.getType());
    }

    public Class<? extends Annotation> getEmQualifier() {
        return emQualifier;
    }

    public Class<?> getType() {
        return type;
    }

}
