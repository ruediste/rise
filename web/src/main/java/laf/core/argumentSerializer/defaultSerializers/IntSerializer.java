package laf.core.argumentSerializer.defaultSerializers;

import java.lang.reflect.AnnotatedType;

import laf.core.argumentSerializer.ArgumentSerializer;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

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
	public Supplier<?> parse(AnnotatedType type, String urlPart) {
		if (!handles(type)) {
			return null;
		}
		if ("null".equals(urlPart)) {
			return () -> null;
		}
		return Suppliers.ofInstance(Integer.parseInt(urlPart));
	}

}
