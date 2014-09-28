package laf.core.argumentSerializer.defaultSerializers.idSerializers;

import javax.persistence.metamodel.Type;

import laf.core.argumentSerializer.defaultSerializers.IdentifierSerializer;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class IntIdSerializer implements IdentifierSerializer {
	private boolean handles(Class<?> type) {
		return type == Integer.class || type == Integer.TYPE;
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
			return () -> null;
		}
		return Suppliers.ofInstance(Integer.parseInt(urlPart));
	}

}
