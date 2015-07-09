package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.function.Supplier;

public class LongSerializer implements ArgumentSerializer {
    @Override
    public boolean handles(AnnotatedType type) {
        return type.getType() == Long.class || type.getType() == Long.TYPE;
    }

    @Override
    public String generate(AnnotatedType type, Object value) {

        return String.valueOf(value);
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
