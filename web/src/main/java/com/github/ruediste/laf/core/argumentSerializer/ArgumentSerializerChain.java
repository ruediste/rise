package com.github.ruediste.laf.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Function;

import com.github.ruediste.laf.core.base.Function2;
import com.google.common.base.Supplier;

/**
 * Combines a list of {@link ArgumentSerializer}s. The serializers are tried one
 * after the other.
 */
public class ArgumentSerializerChain {

	private final Deque<ArgumentSerializer> serializers = new LinkedList<>();

	public void initialize(Iterable<? extends ArgumentSerializer> serializers) {
		for (ArgumentSerializer s : serializers) {
			this.serializers.add(s);
		}
	}

	private <T> T foreach(Function<ArgumentSerializer, T> func) {

		for (ArgumentSerializer s : serializers) {
			T result = func.apply(s);
			if (result != null) {
				return result;
			}
		}
		throw new RuntimeException(
				"No matching argument serializer found for type");

	}

	/**
	 * Generate a string representation which can be parsed later
	 */
	public String generate(AnnotatedType type, Object value) {
		return foreach(x -> x.generate(type, value));
	}

	/**
	 * Parse the string representation of the parameter.
	 */
	public Supplier<?> parse(AnnotatedType type, String urlPart) {
		return foreach(x -> x.parse(type, urlPart));
	}

	public Function2<AnnotatedType, String, Object> parseToObjectFunction() {
		return (a, b) -> parse(a, b).get();
	}
}
