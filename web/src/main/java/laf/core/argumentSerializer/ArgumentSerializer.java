package laf.core.argumentSerializer;

import java.lang.reflect.Type;

import com.google.common.base.Supplier;

public interface ArgumentSerializer {
	/**
	 * Returns true if this handler can handle parameters of the specified type.
	 */
	boolean handles(Type type);

	/**
	 * Generate a string representation which can be parsed later
	 */
	String generate(Type type, Object value);

	/**
	 * Parse the string representation of the parameter.
	 */
	Supplier<?> parse(Type type, String urlPart);
}
