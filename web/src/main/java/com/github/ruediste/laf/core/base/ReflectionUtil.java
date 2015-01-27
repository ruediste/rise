package com.github.ruediste.laf.core.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.inject.spi.BeanManager;

public class ReflectionUtil {

	private ReflectionUtil() {

	}

	public static List<Class<? extends Annotation>> getQualifiers(
			AnnotatedType type, BeanManager beans) {
		List<Class<? extends Annotation>> qualifiers = Arrays
				.asList(type.getAnnotations()).stream()
				.map(x -> x.annotationType()).filter(beans::isQualifier)
				.collect(Collectors.toList());
		return qualifiers;
	}

	/**
	 * Search for an annotation on a method,
	 */
	public static boolean isAnnotationPresent(Class<?> instanceClass,
			Method method, Class<? extends Annotation> annotationClass) {
		Class<?> cls = instanceClass;
		Class<?>[] params = method.getParameterTypes();
		while (cls != null && !cls.equals(Object.class)) {
			methodLoop: for (Method m : cls.getDeclaredMethods()) {
				if (!m.getName().equals(method.getName())) {
					continue;
				}
				Class<?>[] mParams = m.getParameterTypes();
				if (mParams.length != params.length) {
					continue;
				}
				for (int i = 0; i < params.length; i++) {
					if (!mParams[i].equals(params[i])) {
						continue methodLoop;
					}
				}

				// this is the override in this class
				if (m.isAnnotationPresent(annotationClass)) {
					return true;
				}

				break methodLoop;
			}
			cls = cls.getSuperclass();
		}
		return false;
	}
}
