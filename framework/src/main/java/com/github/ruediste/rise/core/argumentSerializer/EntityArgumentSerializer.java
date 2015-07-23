package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;
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

@Singleton
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

    private Map<Optional<Class<? extends Annotation>>, AtomicInteger> nextTypeNrMap = new ConcurrentHashMap<>();
    private Map<Pair<Class<? extends Annotation>, Class<?>>, Integer> typeNrMap = new ConcurrentHashMap<>();
    private Map<Pair<Class<? extends Annotation>, Integer>, Class<?>> parameterTypeMap = new ConcurrentHashMap<>();

    private int getTypeNr(Class<? extends Annotation> qualifier, Class<?> type) {
        Integer result = typeNrMap.computeIfAbsent(
                Pair.of(qualifier, type),
                p -> nextTypeNrMap.computeIfAbsent(
                        Optional.ofNullable(qualifier),
                        q -> new AtomicInteger()).getAndIncrement());
        parameterTypeMap.put(Pair.of(qualifier, result), type);
        return result;
    }

    private Class<?> getType(Class<? extends Annotation> qualifier, int typeNr) {
        return parameterTypeMap.get(Pair.of(qualifier, typeNr));
    }

    @Override
    public Optional<String> generate(AnnotatedType parameterType, Object value) {
        if (value == null)
            return Optional.of(SerializerHelper.generatePrefix(
                    Optional.of("null"),
                    SerializerHelper.generatePrefix(Optional.empty(), "")));

        Class<? extends Annotation> defaultQualifier = getRequiredQualifier(parameterType);

        // try to find entity manager for the value
        Entry<Class<? extends Annotation>, EntityManager> entry;
        {
            Optional<Entry<Class<? extends Annotation>, EntityManager>> emEntry = holder
                    .getEmEntry(value);
            // if no EM found, give up
            if (!emEntry.isPresent())
                return Optional.empty();
            entry = emEntry.get();
        }
        Class<? extends Annotation> acutalQualifier = entry.getKey();

        Object identifier = entry.getValue().getEntityManagerFactory()
                .getPersistenceUnitUtil().getIdentifier(value);

        ManagedType<?> managedType = getManagedType(acutalQualifier,
                value.getClass());

        AnnotatedType idType = getIdType(managedType);

        String serializedIdentifier = config.generateArgument(idType,
                identifier);
        String result;
        if (Objects.equals(acutalQualifier, defaultQualifier)) {
            result = SerializerHelper.generatePrefix(Optional.empty(),
                    serializedIdentifier);
        } else
            result = SerializerHelper.generatePrefix(Optional.of(String
                    .valueOf(dbLinkRegistry.getQualifierNr(acutalQualifier))),
                    serializedIdentifier);

        // include the type of the value in the result if necessary
        ManagedType<?> parameterManagedType = getManagedType(acutalQualifier,
                TypeToken.of(parameterType.getType()).getRawType());
        if (parameterManagedType == null
                || !parameterManagedType.getJavaType().isAssignableFrom(
                        value.getClass()))
            result = SerializerHelper.generatePrefix(Optional.of(Integer
                    .toString(getTypeNr(acutalQualifier, value.getClass()))),
                    result);
        else
            result = SerializerHelper.generatePrefix(Optional.empty(), result);

        return Optional.of(result);
    }

    @Override
    public HandleStatement canHandle(AnnotatedType type) {

        if (TypeToken.of(type.getType()).getRawType().isPrimitive())
            return HandleStatement.CANNOT_HANDLE;
        return HandleStatement.MIGHT_HANDLE;

    }

    @Override
    public Supplier<Object> parse(AnnotatedType type, String urlPart) {

        Optional<String> typeNrStr;
        Optional<String> qualifierStr;
        String idStr;

        {
            Pair<Optional<String>, String> p = SerializerHelper
                    .parsePrefix(urlPart);
            typeNrStr = p.getA();
            p = SerializerHelper.parsePrefix(p.getB());
            qualifierStr = p.getA();
            idStr = p.getB();
        }
        if (Optional.of("null").equals(typeNrStr))
            return () -> null;

        Class<? extends Annotation> qualifier = qualifierStr
                .<Class<? extends Annotation>> map(
                        s -> dbLinkRegistry.getQualifier(Integer.parseInt(s)))
                .orElseGet(() -> getRequiredQualifier(type));

        Class<?> cls = typeNrStr.map(
                s -> getType(qualifier, Integer.parseInt(s))).orElseGet(
                () -> TypeToken.of(type.getType()).getRawType());

        AnnotatedType idType = getIdType(qualifier, cls);

        Supplier<Object> idSupplier = config.parseArgument(idType, idStr);

        return () -> holder.getEntityManager(qualifier).find(cls,
                idSupplier.get());
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
        return getIdType(getManagedType(qualifier, cls));
    }

    private AnnotatedType getIdType(ManagedType<?> managedType) {
        return AnnotatedTypes.of(((IdentifiableType<?>) managedType)
                .getIdType().getJavaType());
    }

    /**
     * Get the {@link ManagedType} or null if the class is not part of the
     * persistence unit.
     */
    private ManagedType<?> getManagedType(
            Class<? extends Annotation> qualifier, Class<?> cls) {
        ManagedType<?> managedType = registry.getManagedTypeMap(qualifier)
                .get().get(cls);
        return managedType;
    }

}
