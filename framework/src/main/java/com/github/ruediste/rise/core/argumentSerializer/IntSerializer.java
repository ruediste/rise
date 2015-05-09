package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.function.Supplier;

public class IntSerializer implements ArgumentSerializer {
	private boolean handles(AnnotatedType type) {
		return type.getType() == Integer.class
				|| type.getType() == Integer.TYPE;
	}

	@Override
	public String generate(AnnotatedType type, Object value) {
		if (!handles(type)) {
			return null;
		}

		return String.valueOf(value);
	}

	@Override
	public Supplier<Object> parse(AnnotatedType type, String urlPart) {
		if (!handles(type)) {
			return null;
		}
		if ("null".equals(urlPart)) {
			return () -> null;
		}
		int value = Integer.parseInt(urlPart);
		return () -> value;
	}

}
