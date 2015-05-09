package com.github.ruediste.rise.core.argumentSerializer;

import java.lang.reflect.AnnotatedType;
import java.util.Base64;
import java.util.function.Supplier;

import com.google.common.base.Charsets;

public class StringSerializer implements ArgumentSerializer {
	private boolean handles(AnnotatedType type) {
		return String.class.equals(type.getType());
	}

	@Override
	public String generate(AnnotatedType type, Object value) {
		if (!handles(type)) {
			return null;
		}

		if (value == null)
			return "~";

		return new String(Base64.getUrlEncoder().encode(
				((String) value).getBytes(Charsets.UTF_8)), Charsets.UTF_8);
	}

	@Override
	public Supplier<Object> parse(AnnotatedType type, String urlPart) {
		if (!handles(type)) {
			return null;
		}
		if ("~".equals(urlPart)) {
			return () -> null;
		}

		String value = new String(Base64.getUrlDecoder().decode(
				urlPart.getBytes(Charsets.UTF_8)), Charsets.UTF_8);
		return () -> value;
	}

}
