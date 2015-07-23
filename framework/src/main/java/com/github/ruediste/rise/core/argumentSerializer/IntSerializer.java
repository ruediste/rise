package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.reflect.TypeToken;

public class IntSerializer implements ArgumentSerializer {
    @Override
    public boolean couldHandle(AnnotatedType type) {
        return type.getType() == Integer.TYPE
                || TypeToken.of(type.getType()).isAssignableFrom(Integer.class);
    }

    @Override
    public Optional<String> generate(AnnotatedType type, Object value) {
        if (value == null)
            return Optional.of("null");
        if (!(value instanceof Integer))
            return Optional.empty();
        return Optional.of(Integer.toString((int) value));
    }

    @Override
    public Supplier<Object> parse(AnnotatedType type, String urlPart) {
        if ("null".equals(urlPart)) {
            return () -> null;
        }
        int value = Integer.parseInt(urlPart);
        return () -> value;
    }

}
