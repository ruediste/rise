package com.github.ruediste.laf.core.argumentSerializer.defaultSerializers;

import java.lang.reflect.AnnotatedType;

import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializer;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class LongSerializer implements ArgumentSerializer {
	private boolean handles(AnnotatedType type) {
		return type.getType() == Long.class || type.getType() == Long.TYPE;
	}

	@Override
	public String generate(AnnotatedType type, Object value) {
		if (!handles(type)) {
			return null;
		}

		return String.valueOf(value);
	}

	@Override
	public Supplier<?> parse(AnnotatedType type, String urlPart) {
		if (!handles(type)) {
			return null;
		}
		if ("null".equals(urlPart)) {
			return () -> null;
		}
		return Suppliers.ofInstance(Long.parseLong(urlPart));
	}

}
