package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.function.Supplier;

public class LongSerializer implements ArgumentSerializer {
    private boolean handles(AnnotatedType type) {
        return type.getType() == Long.class || type.getType() == Long.TYPE;
    }

    @Override
    public String generate(AnnotatedType type, Object value) {
        if (!handles(type)) {
            return null;
        }

        return String.valueOf(value);
    }

    @Override
    public Supplier<Object> parse(AnnotatedType type, String urlPart) {
        if (!handles(type)) {
            return null;
        }
        if ("null".equals(urlPart)) {
            return () -> null;
        }

        long value = Long.parseLong(urlPart);
        return () -> value;
    }

}
