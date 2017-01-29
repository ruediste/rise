package com.github.ruediste.rise.integration;

import static java.util.stream.Collectors.toList;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class StereotypeHelper {
    public static List<Annotation> getAllAnnotations(AnnotatedElement element) {
        return Stream
                .concat(Stream.of(element.getDeclaredAnnotations()),
                        Arrays.stream(element.getDeclaredAnnotations())
                                .filter(a -> a.annotationType().isAnnotationPresent(Stereotype.class))
                                .flatMap(a -> Arrays.stream(a.annotationType().getDeclaredAnnotations())))
                .collect(toList());
    }
}
