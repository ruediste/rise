package laf.core.translation.labels;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.github.ruediste1.c3java.JavaC3;
import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * Contains the
 *
 * @author ruedi
 *
 */
public class PropertyReflectionUtil {

	public static class Property {
		private final String name;
		private final Class<?> declaringType;
		private final Method getter;
		private final Method setter;
		private final Field backingField;

		public Property(String name, Class<?> declaringType, Method getter,
				Method setter, Field backingField) {
			super();
			this.name = name;
			this.declaringType = declaringType;
			this.getter = getter;
			this.setter = setter;
			this.backingField = backingField;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(name, declaringType, getter, setter,
					backingField);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Property other = (Property) obj;
			return Objects.equal(name, other.name)
					&& Objects.equal(declaringType, other.declaringType)
					&& Objects.equal(getter, other.getter)
					&& Objects.equal(setter, other.setter)
					&& Objects.equal(backingField, other.backingField);
		}

		@Override
		public String toString() {
			return java.util.Objects.toString(declaringType) + "::" + name;
		}

		public String getName() {
			return name;
		}

		public Class<?> getDeclaringType() {
			return declaringType;
		}

		public Method getGetter() {
			return getter;
		}

		public Method getSetter() {
			return setter;
		}

		public Field getBackingField() {
			return backingField;
		}

		public Property withGetter(Method getter) {
			return new Property(name, declaringType, getter, setter,
					backingField);
		}

		public Property withSetter(Method setter) {
			return new Property(name, declaringType, getter, setter,
					backingField);
		}

		public Property withBackingField(Field backingField) {
			return new Property(name, declaringType, getter, setter,
					backingField);
		}

	}

	/**
	 * Helper class used to simplify repeated map.put calls
	 */
	private static class Putter {

		private Map<String, Property> map;
		private Class<?> type;

		private Putter(Map<String, Property> map, Class<?> type) {
			this.map = map;
			this.type = type;
		}

		private void put(String key, Function<Property, Property> func) {
			Property property = map.get(key);
			if (property == null) {
				property = new Property(key, type, null, null, null);
			}
			map.put(key, func.apply(property));
		}
	}

	/**
	 * Return all properties which are directly declared on the provided type
	 */
	public Map<String, Property> getDeclaredProperties(Class<?> type) {
		Map<String, Property> result = new HashMap<>();

		// scan methods
		for (Method method : type.getDeclaredMethods()) {
			if (Modifier.isPrivate(method.getModifiers())
					|| Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			Putter putter = new Putter(result, type);

			// check for getters
			if (method.getName().startsWith("get")) {
				String name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
						method.getName().substring("get".length()));

				// getFoo()
				if (method.getParameterCount() == 0) {
					putter.put(name, p -> p.withGetter(method));
					continue;
				}

				// getFoo(1)
				if (method.getParameterCount() == 1
						&& Integer.TYPE.equals(method.getParameterTypes()[0])) {
					putter.put(name, p -> p.withGetter(method));
					continue;
				}

			}

			// check for setters

			if (method.getName().startsWith("set")) {
				String name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
						method.getName().substring("set".length()));
				// setFoo()
				if (method.getParameterCount() == 1) {
					putter.put(name, p -> p.withSetter(method));
					continue;
				}

				// setFoo(1,x)
				if (method.getParameterCount() == 2
						&& Integer.TYPE.equals(method.getParameterTypes()[0])) {
					putter.put(name, p -> p.withSetter(method));
					continue;
				}
			}
		}
		// fill backing fields
		for (Field f : type.getDeclaredFields()) {
			String name = f.getName();
			Property property = result.get(name);
			if (property != null) {
				result.put(name, property.withBackingField(f));
			}
		}
		return result;
	}

	public Property getPropertyIntroduction(Class<?> type, String name) {
		return getPropertyIntroductionMap(type).get(name);
	}

	/**
	 * Return the {@link Property}s of the given type. For each property, the
	 * property declaration which introduced the property is returned.
	 */
	public Map<String, Property> getPropertyIntroductionMap(Class<?> type) {
		Map<String, Property> result = new HashMap<>();

		for (Class<?> cls : Lists.reverse(Lists.newArrayList(JavaC3
				.allSuperclasses(type)))) {
			if (Object.class.equals(cls)) {
				continue;
			}
			for (Property prop : getDeclaredProperties(cls).values()) {
				result.putIfAbsent(prop.getName(), prop);
			}
		}

		return result;
	}

}
