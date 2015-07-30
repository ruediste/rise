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
import javax.persistence.metamodel.ManagedType;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.core.persistence.PersistentTypeIdentifier;
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

    public List<Attribute<?, ?>> getDisplayProperties2(ManagedType<?> type) {
        return new ArrayList<>(type.getAttributes());
    }

    public List<PropertyDeclaration> getEditProperties(Class<?> cls) {
        return PropertyUtil.getPropertyIntroductionMap(cls).values().stream()
                .collect(toList());
    }

    public List<Attribute<?, ?>> getEditProperties2(ManagedType<?> type) {
        return new ArrayList<>(type.getAttributes());
    }

    public List<PropertyDeclaration> getBrowserProperties(Class<?> cls) {
        Preconditions.checkNotNull(cls, "cls is null");
        return getPropertiesAnnotatedWith(cls, CrudBrowserColumn.class);
    }

    public List<Attribute<?, ?>> getBrowserProperties2(
            PersistentTypeIdentifier type) {
        return getBrowserProperties2(persistenceUtil.getManagedType(type));
    }

    public List<Attribute<?, ?>> getBrowserProperties2(ManagedType<?> type) {
        return getPropertiesAnnotatedWith2(type, CrudBrowserColumn.class);
    }

    public List<PropertyDeclaration> getIdentificationProperties(Class<?> cls) {
        return getPropertiesAnnotatedWith(cls, CrudIdentifying.class);
    }

    public List<Attribute<?, ?>> getIdentificationProperties2(
            ManagedType<?> type) {
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

    private List<Attribute<?, ?>> getPropertiesAnnotatedWith2(
            ManagedType<?> type, Class<? extends Annotation> annotationClass) {
        Preconditions.checkNotNull(type, "cls is null");
        ArrayList<Attribute<?, ?>> result = new ArrayList<>();

        for (Attribute<?, ?> attribute : type.getAttributes()) {
            Member member = attribute.getJavaMember();
            if (member instanceof AnnotatedElement)
                if (((AnnotatedElement) member)
                        .isAnnotationPresent(annotationClass))
                    result.add(attribute);
        }
        if (result.isEmpty())
            return new ArrayList<>(type.getAttributes());
        else
            return result;
    }
}
