package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.function.Supplier;

import javax.inject.Inject;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.google.common.reflect.TypeToken;

public class ClassArgumentSerializer implements ArgumentSerializer {

    @Inject
    CoreConfiguration config;

    @Override
    public boolean handles(AnnotatedType type) {
        return Class.class.equals(TypeToken.of(type.getType()).getRawType());
    }

    @Override
    public String generate(AnnotatedType type, Object value) {
        if (value == null)
            return "";
        return ((Class<?>) value).getName();
    }

    @Override
    public Supplier<Object> parse(AnnotatedType type, String urlPart) {
        if ("".equals(urlPart))
            return null;
        return () -> {
            try {
                return config.dynamicClassLoader.loadClass(urlPart);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(
                        "Error while loading class argument <" + urlPart + ">");
            }
        };
    }

}
