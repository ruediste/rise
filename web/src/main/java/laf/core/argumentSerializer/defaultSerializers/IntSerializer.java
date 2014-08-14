package laf.core.argumentSerializer.defaultSerializers;

import java.lang.reflect.Type;

import laf.core.argumentSerializer.ArgumentSerializer;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class IntSerializer implements ArgumentSerializer {
	@Override
	public boolean handles(Type type) {
		return type == Integer.class || type == Integer.TYPE;
	}

	@Override
	public String generate(Type type, Object value) {
		return String.valueOf(value);
	}

	@Override
	public Supplier<?> parse(Type type, String urlPart) {
		return Suppliers.ofInstance(Integer.parseInt(urlPart));
	}

}
