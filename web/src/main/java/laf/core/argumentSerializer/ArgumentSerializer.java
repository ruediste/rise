package laf.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;

import com.google.common.base.Supplier;

public interface ArgumentSerializer {
	/**
	 * Generate a string representation which can be parsed later
	 */
	String generate(AnnotatedType type, Object value);

	/**
	 * Parse the string representation of the parameter.
	 */
	Supplier<?> parse(AnnotatedType type, String urlPart);
}
