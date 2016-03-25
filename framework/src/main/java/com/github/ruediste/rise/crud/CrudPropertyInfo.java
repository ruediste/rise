package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

import javax.persistence.Column;
import javax.persistence.metamodel.Attribute;
import javax.validation.constraints.NotNull;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Provide information about a property, both from a Java and a persistence
 * (JPA) perspective.
 */
public class CrudPropertyInfo {

    private final PropertyInfo property;
    private final Attribute<?, ?> attribute;
    private final Class<? extends Annotation> emQualifier;

    public CrudPropertyInfo(PropertyInfo property, Attribute<?, ?> attribute, Class<? extends Annotation> emQualifier) {
        Preconditions.checkNotNull(property);
        Preconditions.checkNotNull(attribute);
        this.property = property;
        this.attribute = attribute;
        this.emQualifier = emQualifier;
    }

    public String getName() {
        return getAttribute().getName();
    }

    public PropertyInfo getProperty() {
        return property;
    }

    public Attribute<?, ?> getAttribute() {
        return attribute;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(property, attribute, emQualifier);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CrudPropertyInfo))
            return false;
        CrudPropertyInfo other = (CrudPropertyInfo) obj;
        return Objects.equal(property, other.property) && Objects.equal(attribute, other.attribute)
                && Objects.equal(emQualifier, other.emQualifier);
    }

    @Override
    public String toString() {
        return property.toString();
    }

    public Class<? extends Annotation> getEmQualifier() {
        return emQualifier;
    }

    public boolean isOptional() {
        boolean result = true;
        Member javaMember = attribute.getJavaMember();
        if (javaMember instanceof AnnotatedElement) {
            AnnotatedElement element = (AnnotatedElement) javaMember;
            if (element.isAnnotationPresent(NotNull.class))
                result = false;
            Column column = element.getAnnotation(Column.class);
            if (column != null && !column.nullable())
                result = false;
        }
        return result;
    }
}
