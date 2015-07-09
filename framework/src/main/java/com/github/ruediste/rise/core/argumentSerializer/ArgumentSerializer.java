package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.function.Supplier;

/**
 * Serializers used for argument representation in URLs
 */
public interface ArgumentSerializer {

    /**
     * Return true if this serializer could handle a given type
     */
    boolean handles(AnnotatedType type);

    /**
     * Generate a string representation which can be parsed later
     */
    String generate(AnnotatedType type, Object value);

    /**
     * Parse the string representation of the parameter.
     */
    Supplier<Object> parse(AnnotatedType type, String urlPart);
}
