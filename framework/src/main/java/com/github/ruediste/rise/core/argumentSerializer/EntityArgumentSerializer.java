package com.github.ruediste.rise.core.argumentSerializer;

import static java.util.stream.Collectors.toList;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.ManagedType;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.util.AnnotatedTypes;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Ordering;
import com.google.common.reflect.TypeToken;

@Singleton
public class EntityArgumentSerializer implements ArgumentSerializer {

    @Inject
    Injector injector;

    @Inject
    RisePersistenceUtil util;

    @Inject
    CoreConfiguration config;

    Map<Class<? extends Annotation>, BiMap<Class<?>, Integer>> typeNrMap = new ConcurrentHashMap<>();

    private static @interface DefaultQualifier {
    }

    private BiMap<Class<?>, Integer> getTypeNrMap(
            Class<? extends Annotation> qualifier) {
        return typeNrMap.computeIfAbsent(
                qualifier == null ? DefaultQualifier.class : qualifier, q -> {
                    BiMap<Class<?>, Integer> result = HashBiMap.create();
                    List<Class<?>> types = util.getManagedTypeMap(qualifier)
                            .get().keySet().stream()
                            .sorted(Ordering.natural()
                                    .onResultOf(x -> x.getName()))
                            .collect(toList());
                    for (int i = 0; i < types.size(); i++) {
                        result.put(types.get(i), i);
                    }
                    return result;
                });
    }

    private int getTypeNr(Class<? extends Annotation> qualifier,
            Class<?> type) {
        return getTypeNrMap(qualifier).get(type);
    }

    private Class<?> getType(Class<? extends Annotation> qualifier,
            int typeNr) {
        return getTypeNrMap(qualifier).inverse().get(typeNr);
    }

    @Override
    public HandleStatement canHandle(AnnotatedType type) {

        if (TypeToken.of(type.getType()).getRawType().isPrimitive())
            return HandleStatement.CANNOT_HANDLE;
        return HandleStatement.MIGHT_HANDLE;

    }

    @Override
    public Optional<String> generate(AnnotatedType parameterType,
            Object value) {
        if (value == null)
            return Optional.of(SerializerHelper.generatePrefix(
                    Optional.of("null"),
                    SerializerHelper.generatePrefix(Optional.empty(), "")));

        Class<? extends Annotation> defaultQualifier = getRequiredQualifier(
                parameterType);

        // try to find entity manager for the value
        Entry<Class<? extends Annotation>, EntityManager> entry;
        {
            Optional<Entry<Class<? extends Annotation>, EntityManager>> emEntry = util
                    .getEmEntry(value);
            // if no EM found, give up
            if (!emEntry.isPresent())
                return Optional.empty();
            entry = emEntry.get();
        }
        Class<? extends Annotation> acutalQualifier = entry.getKey();

        Object identifier = entry.getValue().getEntityManagerFactory()
                .getPersistenceUnitUtil().getIdentifier(value);

        ManagedType<?> managedType = util.getManagedType(acutalQualifier,
                value.getClass());

        AnnotatedType idType = AnnotatedTypes.of(util.getIdType(managedType));

        String serializedIdentifier = config.generateArgument(idType,
                identifier);
        String result;
        if (Objects.equals(acutalQualifier, defaultQualifier)) {
            result = SerializerHelper.generatePrefix(Optional.empty(),
                    serializedIdentifier);
        } else
            result = SerializerHelper.generatePrefix(
                    Optional.of(String
                            .valueOf(util.getQualifierNr(acutalQualifier))),
                    serializedIdentifier);

        // include the type of the value in the result if necessary
        ManagedType<?> parameterManagedType = util
                .getManagedType(acutalQualifier, parameterType.getType());
        if (parameterManagedType == null || !parameterManagedType.getJavaType()
                .isAssignableFrom(value.getClass()))
            result = SerializerHelper.generatePrefix(
                    Optional.of(Integer.toString(
                            getTypeNr(acutalQualifier, value.getClass()))),
                    result);
        else
            result = SerializerHelper.generatePrefix(Optional.empty(), result);

        return Optional.of(result);
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
                        s -> util.getQualifierByNr(Integer.parseInt(s)))
                .orElseGet(() -> getRequiredQualifier(type));

        Class<?> cls = typeNrStr
                .<Class<?>> map(s -> getType(qualifier, Integer.parseInt(s)))
                .orElseGet(() -> TypeToken.of(type.getType()).getRawType());

        AnnotatedType idType = AnnotatedTypes
                .of(util.getIdType(qualifier, cls));

        Supplier<Object> idSupplier = config.parseArgument(idType, idStr);

        return () -> util.getEntityManager(qualifier).find(cls,
                idSupplier.get());
    }

    private Class<? extends Annotation> getRequiredQualifier(
            AnnotatedType type) {
        Annotation qualifierAnnotation = injector.getDelegate().getConfig()
                .getRequiredQualifier(type, type);

        Class<? extends Annotation> qualifier = qualifierAnnotation == null
                ? null : qualifierAnnotation.annotationType();
        return qualifier;
    }

}
