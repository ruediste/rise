package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Charsets;
import com.google.common.reflect.TypeToken;

public class SerializableSerializer implements ArgumentSerializer {
    @Override
    public boolean couldHandle(AnnotatedType type) {
        return TypeToken.of(type.getType()).isAssignableFrom(String.class);
    }
asdf
    @Override
    public Optional<String> generate(AnnotatedType type, Object value) {

        if (value == null)
            return Optional.of("~");
        if (!(value instanceof String))
            return Optional.empty();

        return Optional.of(new String(Base64.getUrlEncoder().encode(
                ((String) value).getBytes(Charsets.UTF_8)), Charsets.UTF_8));
    }

    @Override
    public Supplier<Object> parse(AnnotatedType type, String urlPart) {
        if ("~".equals(urlPart)) {
            return () -> null;
        }

        String value = new String(Base64.getUrlDecoder().decode(
                urlPart.getBytes(Charsets.UTF_8)), Charsets.UTF_8);
        return () -> value;
    }

}
