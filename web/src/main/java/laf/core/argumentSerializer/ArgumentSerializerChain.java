package laf.core.argumentSerializer;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import laf.base.Function2;

import com.google.common.base.Supplier;

/**
 * Combines a list of {@link ArgumentSerializer}s. The serializers are tried one
 * after the other.
 */
public class ArgumentSerializerChain {

	private final Deque<ArgumentSerializer> serializers = new LinkedList<>();
	private final ConcurrentMap<Type, ArgumentSerializer> map = new ConcurrentHashMap<>();

	public void initialize(Iterable<? extends ArgumentSerializer> serializers) {
		for (ArgumentSerializer s : serializers) {
			this.serializers.add(s);
		}
	}

	private ArgumentSerializer getSerializer(Type type) {
		ArgumentSerializer argumentSerializer = map.get(type);
		if (argumentSerializer == null) {
			for (ArgumentSerializer s : serializers) {
				if (s.handles(type)) {
					return map.putIfAbsent(type, s);
				}
			}
			throw new RuntimeException("No argument serializer found for type "
					+ type);
		} else {
			return argumentSerializer;
		}

	}

	/**
	 * Generate a string representation which can be parsed later
	 */
	public String generate(Type type, Object value) {
		return getSerializer(type).generate(type, value);
	}

	/**
	 * Parse the string representation of the parameter.
	 */
	public Supplier<?> parse(Type type, String urlPart) {
		return getSerializer(type).parse(type, urlPart);
	}

	public Function2<Type, Object, String> generateFunction() {
		return new Function2<Type, Object, String>() {

			@Override
			public String apply(Type a, Object b) {
				return generate(a, b);
			}
		};

	}

	public Function2<Type, String, Supplier<?>> parseFunction() {
		return new Function2<Type, String, Supplier<?>>() {

			@Override
			public Supplier<?> apply(Type a, String b) {
				return parse(a, b);
			}
		};
	}

	public Function2<Type, String, Object> parseToObjectFunction() {
		return new Function2<Type, String, Object>() {

			@Override
			public Object apply(Type a, String b) {
				return parse(a, b).get();
			}
		};
	}
}
