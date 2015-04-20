package com.github.ruediste.laf.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.function.Supplier;

public interface ArgumentSerializer {

	/**
	 * Generate a string representation which can be parsed later
	 */
	String generate(AnnotatedType type, Object value);

	/**
	 * Parse the string representation of the parameter.
	 */
	Supplier<Object> parse(AnnotatedType type, String urlPart);
}
