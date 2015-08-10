package com.github.ruediste.rise.crud;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.crud.annotations.CrudBrowserColumn;
import com.github.ruediste.rise.crud.annotations.CrudIdentifying;
import com.github.ruediste.rise.nonReloadable.front.reload.MemberOrderIndex;
import com.google.common.base.Preconditions;

@Singleton
public class CrudReflectionUtil {

    @Inject
    RisePersistenceUtil persistenceUtil;

    @Inject
    MemberOrderIndex memberOrderIndex;

    public List<PersistentProperty> getDisplayProperties(PersistentType type) {
        return getAllProperties(type);
    }

    public List<PersistentProperty> getEditProperties(PersistentType type) {
        return getAllProperties(type);
    }

    public Map<String, PersistentProperty> getAllPropertiesMap(
            PersistentType type) {
        return getAllProperties(type).stream().collect(
                toMap(x -> x.getAttribute().getName(), x -> x));
    }

    public List<PersistentProperty> getAllProperties(PersistentType type) {
        return getOrderedAttributes(type.getType()).stream()
                .map(this::toPersistentProperty).collect(toList());
    }

    public List<PersistentProperty> getBrowserProperties(PersistentType type) {
        return getPropertiesAnnotatedWith(type, CrudBrowserColumn.class,
                CrudIdentifying.class);
    }

    public List<PersistentProperty> getIdentificationProperties(
            PersistentType type) {
        return getPropertiesAnnotatedWith(type, CrudIdentifying.class);
    }

    @SafeVarargs
    final private List<PersistentProperty> getPropertiesAnnotatedWith(
            PersistentType type,
            Class<? extends Annotation>... annotationClasses) {
        Preconditions.checkNotNull(type, "cls is null");
        List<Attribute<?, ?>> result = new ArrayList<>();

        ManagedType<?> type2 = type.getType();
        List<Attribute<?, ?>> orderedAttributes = getOrderedAttributes(type2);
        attributeLoop: for (Attribute<?, ?> attribute : orderedAttributes) {
            Member member = attribute.getJavaMember();
            if (member instanceof AnnotatedElement)
                for (Class<? extends Annotation> annotationClass : annotationClasses)
                    if (((AnnotatedElement) member)
                            .isAnnotationPresent(annotationClass)) {
                        result.add(attribute);
                        continue attributeLoop;
                    }
        }
        if (result.isEmpty())
            result = orderedAttributes;
        return result.stream().map(this::toPersistentProperty)
                .collect(toList());
    }

    private List<Attribute<?, ?>> getOrderedAttributes(ManagedType<?> type2) {
        Map<Member, Attribute<?, ?>> memberMap = new HashMap<>();
        for (Attribute<?, ?> attr : type2.getAttributes()) {
            memberMap.put(attr.getJavaMember(), attr);
        }
        ArrayList<Attribute<?, ?>> result = new ArrayList<>();
        for (Member member : memberOrderIndex.orderMembers(type2.getJavaType(),
                memberMap.keySet())) {
            result.add(memberMap.get(member));
        }
        return result;
    }

    public PersistentProperty toPersistentProperty(Attribute<?, ?> attribute) {
        PropertyInfo property = PropertyUtil.getPropertyInfo(attribute
                .getDeclaringType().getJavaType(), attribute.getName());

        return new PersistentProperty(property, attribute);
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
