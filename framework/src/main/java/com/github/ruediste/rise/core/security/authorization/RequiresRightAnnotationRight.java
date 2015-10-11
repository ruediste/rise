package com.github.ruediste.rise.core.security.authorization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Right representing an annotation occurence of a {@link MetaRequiresRight}
 * annotation.
 */
public class RequiresRightAnnotationRight implements Right {

    private final Object value;
    private final Annotation annotation;
    private final Method method;

    public RequiresRightAnnotationRight(Object value, Method method,
            Annotation annotation) {
        super();
        this.value = value;
        this.method = method;
        this.annotation = annotation;
    }

    /**
     * The value given by the annotation. In case the value element of the
     * annotation is of array type, this will be a component of the array.
     */
    public Object getValue() {
        return value;
    }

    /**
     * The annotation the right is derived from
     */
    public Annotation getAnnotation() {
        return annotation;
    }

    /**
     * The method the annotation is placed on
     */
    public Method getMethod() {
        return method;
    }

}
