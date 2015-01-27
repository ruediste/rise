package com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.idSerializers;

import javax.persistence.metamodel.Type;

import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.IdentifierSerializer;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class LongIdSerializer implements IdentifierSerializer {
	private boolean handles(Class<?> type) {
		return type == Long.class || type == Long.TYPE;
	}

	@Override
	public String generate(Type<?> idType, Object value) {
		if (!handles(idType.getJavaType())) {
			return null;
		}

		return String.valueOf(value);
	}

	@Override
	public Supplier<?> parse(Type<?> idType, String urlPart) {
		if (!handles(idType.getJavaType())) {
			return null;
		}
		if ("null".equals(urlPart)) {
			return Suppliers.ofInstance(null);
		}
		return Suppliers.ofInstance(Long.parseLong(urlPart));
	}

}
