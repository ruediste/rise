package com.github.ruediste.rise.crud;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

import javax.persistence.metamodel.Attribute;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class PersistentProperty {

    private final PropertyInfo property;
    private final Attribute<?, ?> attribute;

    public PersistentProperty(PropertyInfo property, Attribute<?, ?> attribute) {
        Preconditions.checkNotNull(property);
        Preconditions.checkNotNull(attribute);
        this.property = property;
        this.attribute = attribute;
    }

    public Object getValue(Object entity) {
        Member member = attribute.getJavaMember();
        if (member instanceof Field) {
            Field field = (Field) member;
            field.setAccessible(true);
            try {
                return field.get(entity);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException("Error while getting field " + field);
            }
        } else
            throw new RuntimeException("Unknown member type " + member);
    }

    public void setValue(Object entity, Object value) {
        Member member = attribute.getJavaMember();
        if (member instanceof Field) {
            Field field = (Field) member;
            field.setAccessible(true);
            try {
                field.set(entity, value);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException("Error while setting field " + field);
            }
        } else
            throw new RuntimeException("Unknown member type " + member);
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
        return Objects.hashCode(property, attribute);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof PersistentProperty))
            return false;
        PersistentProperty other = (PersistentProperty) obj;
        return Objects.equal(property, other.property)
                && Objects.equal(attribute, other.attribute);
    }

    @Override
    public String toString() {
        return property.toString();
    }
}
