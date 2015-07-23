package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Serializers used for argument representation in URLs
 */
public interface ArgumentSerializer {

    /**
     * Describes if a serializer handles a given parameter
     */
    public enum HandleStatement {
        CANNOT_HANDLE, MIGHT_HANDLE, WILL_HANDLE
    }

    /**
     * determine if this serializer handles a parameter of a type
     */
    HandleStatement canHandle(AnnotatedType type);

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
