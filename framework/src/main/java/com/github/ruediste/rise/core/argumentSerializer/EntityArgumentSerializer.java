package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.core.persistence.em.PersisteUnitRegistry;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.util.AnnotatedTypes;
import com.github.ruediste.rise.util.Pair;
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

    @Override
    public String generate(AnnotatedType type, Object value) {
        if (value == null)
            return SerializerHelper.generatePrefix(Optional.of("null"), "");

        Class<? extends Annotation> defaultQualifier = getRequiredQualifier(type);
        Entry<Class<? extends Annotation>, EntityManager> entry = holder
                .getEmEntry(value).get();
        Class<? extends Annotation> acutalQualifier = entry.getKey();

        Object identifier = entry.getValue().getEntityManagerFactory()
                .getPersistenceUnitUtil().getIdentifier(value);

        AnnotatedType idType = getIdType(acutalQualifier, type);

        String serializedIdentifier = config.generateArgument(idType,
                identifier);
        if (Objects.equals(acutalQualifier, defaultQualifier)) {
            return SerializerHelper.generatePrefix(Optional.empty(),
                    serializedIdentifier);
        } else
            return SerializerHelper.generatePrefix(Optional.of(String
                    .valueOf(dbLinkRegistry.getQualifierNr(acutalQualifier))),
                    serializedIdentifier);
    }

    @Override
    public boolean handles(AnnotatedType type) {
        return type.isAnnotationPresent(Entity.class);
    }

    @Override
    public Supplier<Object> parse(AnnotatedType type, String urlPart) {

        return () -> {
            Pair<Optional<String>, String> pair = SerializerHelper
                    .parsePrefix(urlPart);
            if (Optional.of("null").equals(pair.getA()))
                return null;

            Class<? extends Annotation> qualifier = pair
                    .getA()
                    .<Class<? extends Annotation>> map(
                            s -> dbLinkRegistry.getQualifier(Integer
                                    .parseInt(s)))
                    .orElseGet(() -> getRequiredQualifier(type));

            Class<?> cls = TypeToken.of(type.getType()).getRawType();

            AnnotatedType idType = getIdType(qualifier, cls);

            Supplier<Object> idSupplier = config.parseArgument(idType,
                    pair.getB());

            return holder.getEntityManager(qualifier).find(cls,
                    idSupplier.get());
        };
    }

    private Class<? extends Annotation> getRequiredQualifier(AnnotatedType type) {
        Annotation qualifierAnnotation = injector.getDelegate().getConfig()
                .getRequiredQualifier(type, type);

        Class<? extends Annotation> qualifier = qualifierAnnotation == null ? null
                : qualifierAnnotation.annotationType();
        return qualifier;
    }

    private AnnotatedType getIdType(Class<? extends Annotation> qualifier,
            AnnotatedType type) {
        return getIdType(qualifier, TypeToken.of(type.getType()).getRawType());
    }

    private AnnotatedType getIdType(Class<? extends Annotation> qualifier,
            Class<?> cls) {
        ManagedType<?> managedType = registry.getManagedTypeMap(qualifier)
                .get().get(cls);
        return AnnotatedTypes.of(((IdentifiableType<?>) managedType)
                .getIdType().getJavaType());
    }

}
