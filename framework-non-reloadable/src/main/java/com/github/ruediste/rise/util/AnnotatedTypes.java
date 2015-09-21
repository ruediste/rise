package com.github.ruediste.rise.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

/**
 * Helper class to create instances of {@link AnnotatedType}
 */
public class AnnotatedTypes {

    public static AnnotatedType of(Class<?> t, Annotation... annotations) {
        return new AnnotatedTypeImpl(t, annotations);
    }

    public static AnnotatedType of(Type t, Annotation... annotations) {
        return new AnnotatedTypeImpl(t, annotations);
    }

    private static class AnnotatedTypeImpl implements AnnotatedType {

        private Annotation[] annotations;
        private Type type;

        AnnotatedTypeImpl(Type type, Annotation... annotations) {
            super();
            this.annotations = annotations;
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Annotation> T getAnnotation(
                Class<T> annotationClass) {
            for (Annotation a : annotations) {
                if (annotationClass.equals(a.annotationType()))
                    return (T) a;
            }
            return null;
        }

        @Override
        public Annotation[] getAnnotations() {
            return annotations;
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return annotations;
        }

        @Override
        public Type getType() {
            return type;
        }

    }
}
