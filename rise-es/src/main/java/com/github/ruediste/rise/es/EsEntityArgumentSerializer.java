package com.github.ruediste.rise.es;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.AnnotatedType;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.codec.Charsets;

import com.github.ruediste.rise.core.argumentSerializer.ArgumentSerializer;
import com.github.ruediste.rise.core.argumentSerializer.SerializerHelper;
import com.github.ruediste.rise.es.api.EsEntity;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.util.Pair;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.BaseEncoding;
import com.google.common.reflect.TypeToken;

public class EsEntityArgumentSerializer implements ArgumentSerializer {

    @Inject
    ClassHierarchyIndex idx;

    @Inject
    ClassLoader cl;

    @Inject
    EsHelper es;

    BiMap<Class<? extends EsEntity>, Integer> entityClassNumbers = HashBiMap.create();

    @SuppressWarnings("rawtypes")
    @PostConstruct
    public void postConstruct() {

        List<Class<? extends EsEntity>> entityClasses = idx.getAllChildClasses(EsEntity.class, cl).stream()
                .sorted(Comparator.comparing(c -> c.getName())).collect(toList());
        for (int i = 0; i < entityClasses.size(); i++) {
            Class<? extends EsEntity> cls = entityClasses.get(i);
            entityClassNumbers.put(cls, i);
        }

    }

    @Override
    public HandleStatement canHandle(AnnotatedType type) {
        if (entityClassNumbers.containsKey(raw(type)))
            return HandleStatement.WILL_HANDLE;
        return HandleStatement.CANNOT_HANDLE;
    }

    private Class<?> raw(AnnotatedType type) {
        return TypeToken.of(type.getType()).getRawType();
    }

    @Override
    public Supplier<Object> parse(AnnotatedType type, String urlPart) {
        Pair<Optional<String>, String> pair = SerializerHelper.parsePrefix(urlPart);
        String prefix = pair.getA().get();
        if ("null".equals(prefix))
            return null;

        @SuppressWarnings("rawtypes")
        Class<? extends EsEntity> cls = entityClassNumbers.inverse().get(Integer.parseInt(prefix));
        String id = new String(BaseEncoding.base64Url().decode(pair.getB()), Charsets.UTF_8);

        return () -> {
            return es.get(cls, id).orElseThrow(() -> new RuntimeException("Entity not found: " + cls + " id: " + id));
        };
    }

    @Override
    public Optional<String> generate(AnnotatedType type, Object value) {
        if (value == null)
            return Optional.of(SerializerHelper.generatePrefix(Optional.of("null"), ""));
        Integer nr = entityClassNumbers.get(raw(type));
        @SuppressWarnings("rawtypes")
        String id = BaseEncoding.base64Url().omitPadding().encode(((EsEntity) value).getId().getBytes(Charsets.UTF_8));
        return Optional.of(SerializerHelper.generatePrefix(Optional.of(nr.toString()), id));
    }
}