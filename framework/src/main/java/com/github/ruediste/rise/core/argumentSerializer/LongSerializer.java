package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.reflect.TypeToken;

public class LongSerializer implements ArgumentSerializer {
    @Override
    public boolean couldHandle(AnnotatedType type) {

        return type.getType() == Long.TYPE
                || TypeToken.of(type.getType()).isAssignableFrom(Long.class);
    }

    @Override
    public Optional<String> generate(AnnotatedType type, Object value) {
        if (value == null)
            return Optional.of("null");
        if (!(value instanceof Long))
            return Optional.empty();
        return Optional.of(Long.toString((long) value));
    }

    @Override
    public Supplier<Object> parse(AnnotatedType type, String urlPart) {
        if ("null".equals(urlPart)) {
            return () -> null;
        }

        long value = Long.parseLong(urlPart);
        return () -> value;
    }

}
