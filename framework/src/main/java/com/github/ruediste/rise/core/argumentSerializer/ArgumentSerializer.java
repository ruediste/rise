package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Serializers used for argument representation in URLs
 */
public interface ArgumentSerializer {

    /**
     * Return true if this serializer could handle a given type.
     */
    boolean couldHandle(AnnotatedType type);

    /**
     * Generate a string representation which can be parsed later. If
     * {@link Optional#empty()} is returned, the next serializer is tried
     */
    Optional<String> generate(AnnotatedType type, Object value);

    /**
     * Parse the string representation of the parameter.
     */
    Supplier<Object> parse(AnnotatedType type, String urlPart);
}
