package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Type.PersistenceType;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.core.persistence.em.PersisteUnitRegistry;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.util.AnnotatedTypes;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.reflect.TypeToken;

public class EntityArgumentSerializer implements ArgumentSerializer {

    @Inject
    Injector injector;

    @Inject
    PersisteUnitRegistry registry;

    @Inject
    DataBaseLinkRegistry dbLinkRegistry;

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
        if (value == null)
            return "null";

        Class<? extends Annotation> defaultQualifier = getRequiredQualifier(type);
        Entry<Class<? extends Annotation>, EntityManager> entry = holder
                .getEmEntry(value).get();
        Class<? extends Annotation> acutalQualifier = entry.getKey();

        Object identifier = entry.getValue().getEntityManagerFactory()
                .getPersistenceUnitUtil().getIdentifier(value);

        AnnotatedType idType = getIdType(acutalQualifier, value);

        String serializedIdentifier = config.generateArgument(idType,
                identifier);
        if (Objects.equals(acutalQualifier, defaultQualifier)) {
            if (serializedIdentifier.contains(":"))
                return ":" + serializedIdentifier;
            else
                return serializedIdentifier;
        } else
            return dbLinkRegistry.getQualifierNr(acutalQualifier) + ":"
                    + serializedIdentifier;
    }

    private AnnotatedType getIdType(Class<? extends Annotation> qualifier,
            Object value) {
        ManagedType<?> managedType = registry.getManagedTypeMap(qualifier)
                .get().get(value.getClass());
        return AnnotatedTypes.of(((IdentifiableType<?>) managedType)
                .getIdType().getJavaType());
    }

    @Override
    public boolean handles(AnnotatedType type) {
        Info info = createInfo(type);
        return info != null;
    }

    @Override
    public Supplier<Object> parse(AnnotatedType type, String urlPart) {

        Info info = createInfo(type);

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
                TypeToken.of(type.getType()).getRawType(), idSupplier.get());
    }

    private Info getIdType(AnnotatedType type) {

        Info info = new Info();

        Class<? extends Annotation> qualifier = getRequiredQualifier(type);

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

    private Info createInfo(AnnotatedType type) {

        Info info = new Info();

        Class<? extends Annotation> qualifier = getRequiredQualifier(type);

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

    private Class<? extends Annotation> getRequiredQualifier(AnnotatedType type) {
        Annotation qualifierAnnotation = injector.getDelegate().getConfig()
                .getRequiredQualifier(type, type);

        Class<? extends Annotation> qualifier = qualifierAnnotation == null ? null
                : qualifierAnnotation.annotationType();
        return qualifier;
    }

}
