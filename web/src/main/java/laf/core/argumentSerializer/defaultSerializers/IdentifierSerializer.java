package laf.core.argumentSerializer.defaultSerializers;

import javax.persistence.metamodel.Type;

import com.google.common.base.Supplier;

public interface IdentifierSerializer {
	/**
	 * Generate a string representation which can be parsed later
	 */
	String generate(Type<?> idType, Object value);

	/**
	 * Parse the string representation of the parameter.
	 */
	Supplier<?> parse(Type<?> idType, String urlPart);
}
