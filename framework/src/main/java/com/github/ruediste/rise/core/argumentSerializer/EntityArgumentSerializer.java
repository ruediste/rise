package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Type.PersistenceType;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.core.persistence.em.PersisteUnitRegistry;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.reflect.TypeToken;

public class EntityArgumentSerializer implements ArgumentSerializer {

    @Inject
    Injector injector;

    @Inject
    PersisteUnitRegistry registry;

    @Inject
    CoreConfiguration config;

    @Inject
    EntityManagerHolder holder;

    private AtomicLong nextTypeNr = new AtomicLong();

    private static class EntityTypeEntry {
        AnnotatedType idType;
        Class<?> entityType;
        public long typeNr;
    }

    private Object lock = new Object();
    private HashMap<Long, EntityTypeEntry> typeEntriesByNr = new HashMap<>();
    private HashMap<Class<?>, EntityTypeEntry> typeEntriesByClass = new HashMap<>();

    private static class Info {
        public EntityManagerFactory unit;
        public Class<? extends Annotation> qualifier;
    }

    @Override
    public String generate(AnnotatedType type, Object value) {

        Info info = createInfo(type);
        if (info == null)
            return null;

        if (value == null)
            return "null";

        EntityTypeEntry entry;
        synchronized (lock) {
            entry = typeEntriesByClass.get(value.getClass());
            if (entry == null) {
                entry = new EntityTypeEntry();
                entry.entityType = value.getClass();
                entry.idType = getIdType(info.qualifier, value);
                entry.typeNr = nextTypeNr.getAndIncrement();
                typeEntriesByClass.put(entry.entityType, entry);
                typeEntriesByNr.put(entry.typeNr, entry);
            }
        }

        Object identifier = info.unit.getPersistenceUnitUtil().getIdentifier(
                value);

        String serializedIdentifier = config.generateArgument(entry.idType,
                identifier);
        if (type.getType() == entry.entityType) {
            return ":" + serializedIdentifier;
        } else
            return entry.typeNr + ":" + serializedIdentifier;
    }

    private AnnotatedType getIdType(Class<? extends Annotation> qualifier,
            Object value) {
        ManagedType<?> managedType = registry.getManagedTypeMap(qualifier)
                .get().get(value.getClass());
        return new AnnotatedType() {

            @Override
            public Annotation[] getDeclaredAnnotations() {
                return new Annotation[] {};
            }

            @Override
            public Annotation[] getAnnotations() {
                return new Annotation[] {};
            }

            @Override
            public <T extends Annotation> T getAnnotation(
                    Class<T> annotationClass) {
                return null;
            }

            @Override
            public Type getType() {
                return ((IdentifiableType<?>) managedType).getIdType()
                        .getJavaType();
            }
        };
    }

    @Override
    public Supplier<Object> parse(AnnotatedType type, String urlPart) {

        Info info = createInfo(type);
        if (info == null)
            return null;
        if (urlPart == "null")
            return () -> null;

        int idx = urlPart.indexOf(':');
        if (idx < 0)
            throw new RuntimeException("Invalid URL part for entity argument: "
                    + urlPart);

        EntityTypeEntry entry;
        if (idx == 0) {
            entry = typeEntriesByClass.get(type.getType());
        } else {
            long classId = Long.parseLong(urlPart.substring(0, idx));
            entry = typeEntriesByNr.get(classId);
        }

        Supplier<Object> idSupplier = config.parseArgument(entry.idType,
                urlPart.substring(idx + 1, urlPart.length()));

        return () -> holder.getEntityManager(info.qualifier).find(
                entry.entityType, idSupplier.get());
    }

    private Info createInfo(AnnotatedType type) {

        Info info = new Info();

        Annotation qualifierAnnotation = injector.getDelegate().getConfig()
                .getRequiredQualifier(type, type);

        Class<? extends Annotation> qualifier = qualifierAnnotation == null ? null
                : qualifierAnnotation.annotationType();
        info.qualifier = qualifier;

        Optional<Map<Class<?>, ManagedType<?>>> typesOptional = registry
                .getManagedTypeMap(qualifier);

        if (!typesOptional.isPresent())
            return null;

        info.unit = registry.getUnit(qualifier).get();

        Map<Class<?>, ManagedType<?>> types = typesOptional.get();
        ManagedType<?> managedType = types.get(TypeToken.of(type.getType())
                .getRawType());

        if (managedType == null)
            return null;
        if (managedType.getPersistenceType() != PersistenceType.ENTITY
                && managedType.getPersistenceType() != PersistenceType.MAPPED_SUPERCLASS)
            return null;

        return info;
    }

}
