package com.github.ruediste.rise.core.argumentSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.AnnotatedType;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Charsets;
import com.google.common.reflect.TypeToken;

public class SerializableArgumentSerializer implements ArgumentSerializer {
    @Override
    public HandleStatement canHandle(AnnotatedType type) {
        if (Serializable.class.isAssignableFrom(TypeToken.of(type.getType())
                .getRawType()))
            return HandleStatement.WILL_HANDLE;
        return HandleStatement.MIGHT_HANDLE;
    }

    @Override
    public Optional<String> generate(AnnotatedType type, Object value) {
        if (value == null)
            return Optional.of("~");
        if (!(value instanceof Serializable))
            return Optional.empty();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(value);
        } catch (IOException e) {
            throw new RuntimeException("Error while serializing " + value, e);
        }
        return Optional.of(new String(Base64.getUrlEncoder().encode(
                baos.toByteArray()), Charsets.UTF_8));
    }

    @Override
    public Supplier<Object> parse(AnnotatedType type, String urlPart) {
        if ("~".equals(urlPart)) {
            return () -> null;
        }

        byte[] input = Base64.getUrlDecoder().decode(
                urlPart.getBytes(Charsets.UTF_8));

        Object result;
        try {
            result = new ObjectInputStream(new ByteArrayInputStream(input))
                    .readObject();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while decoding serialized parameter " + type, e);
        }
        return () -> result;
    }

}
