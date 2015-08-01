package com.github.ruediste.rise.crud;

import static java.util.stream.Collectors.toList;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.Attribute;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.crud.annotations.CrudBrowserColumn;
import com.github.ruediste.rise.crud.annotations.CrudIdentifying;
import com.google.common.base.Preconditions;

@Singleton
public class CrudReflectionUtil {

    @Inject
    RisePersistenceUtil persistenceUtil;

    public PropertyInfo getProperty(Attribute<?, ?> attribute) {
        return PropertyUtil.getPropertyInfo(attribute.getDeclaringType()
                .getJavaType(), attribute.getName());
    }

    public List<PropertyDeclaration> getDisplayProperties(Class<?> cls) {
        return new ArrayList<>(PropertyUtil.getPropertyIntroductionMap(cls)
                .values());
    }

    public List<PersistentProperty> getDisplayProperties2(PersistentType type) {
        return getAllProperties(type);
    }

    public List<PropertyDeclaration> getEditProperties(Class<?> cls) {
        return PropertyUtil.getPropertyIntroductionMap(cls).values().stream()
                .collect(toList());
    }

    public List<PersistentProperty> getEditProperties2(PersistentType type) {
        return getAllProperties(type);
    }

    private List<PersistentProperty> getAllProperties(PersistentType type) {
        return type.getType().getAttributes().stream()
                .map(this::toPersistentAttribute).collect(toList());
    }

    public List<PropertyDeclaration> getBrowserProperties(Class<?> cls) {
        Preconditions.checkNotNull(cls, "cls is null");
        return getPropertiesAnnotatedWith(cls, CrudBrowserColumn.class);
    }

    public List<PersistentProperty> getBrowserProperties2(PersistentType type) {
        return getPropertiesAnnotatedWith2(type, CrudBrowserColumn.class);
    }

    public List<PropertyDeclaration> getIdentificationProperties(Class<?> cls) {
        return getPropertiesAnnotatedWith(cls, CrudIdentifying.class);
    }

    public List<PersistentProperty> getIdentificationProperties2(
            PersistentType type) {
        return getPropertiesAnnotatedWith2(type, CrudBrowserColumn.class);
    }

    private List<PropertyDeclaration> getPropertiesAnnotatedWith(Class<?> cls,
            Class<? extends Annotation> annotationClass) {
        Preconditions.checkNotNull(cls, "cls is null");
        ArrayList<PropertyDeclaration> result = new ArrayList<>();

        Collection<PropertyDeclaration> allDeclarations = PropertyUtil
                .getPropertyIntroductionMap(cls).values();
        for (PropertyDeclaration declaration : allDeclarations) {
            Field backingField = declaration.getBackingField();
            if (backingField == null)
                continue;
            if (backingField.isAnnotationPresent(annotationClass))
                result.add(declaration);
        }
        if (result.isEmpty())
            return new ArrayList<>(allDeclarations);
        else
            return result;
    }

    private List<PersistentProperty> getPropertiesAnnotatedWith2(
            PersistentType type, Class<? extends Annotation> annotationClass) {
        Preconditions.checkNotNull(type, "cls is null");
        List<Attribute<?, ?>> result = new ArrayList<>();

        for (Attribute<?, ?> attribute : type.getType().getAttributes()) {
            Member member = attribute.getJavaMember();
            if (member instanceof AnnotatedElement)
                if (((AnnotatedElement) member)
                        .isAnnotationPresent(annotationClass))
                    result.add(attribute);
        }
        if (result.isEmpty())
            result = new ArrayList<>(type.getType().getAttributes());
        return result.stream().map(this::toPersistentAttribute)
                .collect(toList());
    }

    public PersistentProperty toPersistentAttribute(Attribute<?, ?> attribute) {
        return new PersistentProperty(PropertyUtil.getPropertyInfo(attribute
                .getDeclaringType().getJavaType(), attribute.getName()),
                attribute);
    }

    public PersistentType getPersistentType(Object entity) {
        return getPersistentType(persistenceUtil.getEmQualifier(entity),
                entity.getClass());
    }

    public PersistentType getPersistentType(
            Class<? extends Annotation> emQualifier, Class<?> entityClass) {
        return new PersistentType(emQualifier, entityClass,
                persistenceUtil.getManagedType(emQualifier, entityClass));
    }
}
