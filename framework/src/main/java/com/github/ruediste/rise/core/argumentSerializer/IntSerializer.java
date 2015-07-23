package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.reflect.TypeToken;

public class IntSerializer implements ArgumentSerializer {
    @Override
    public HandleStatement canHandle(AnnotatedType type) {
        if (type.getType() == Integer.TYPE)
            return HandleStatement.WILL_HANDLE;
        TypeToken<?> token = TypeToken.of(type.getType());
        if (Integer.class.isAssignableFrom(token.getRawType()))
            return HandleStatement.WILL_HANDLE;
        if (token.isAssignableFrom(Integer.class))
            return HandleStatement.MIGHT_HANDLE;
        return HandleStatement.CANNOT_HANDLE;
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
