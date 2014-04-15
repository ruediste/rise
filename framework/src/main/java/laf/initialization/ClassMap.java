package laf.initialization;

import java.util.*;

import com.google.common.collect.HashMultimap;

/**
 * A map from classes to values. The map respects the class inheritance of the
 * provided class keys. A value put using class A as key will be returned when
 * querying for A or any of it's super classes.
 *
 */
public class ClassMap<K, V> {

	HashMultimap<Class<?>, V> map = HashMultimap.create();

	/**
	 * Cache for super classes.
	 */
	HashMap<Class<?>, Set<Class<?>>> superClasses = new HashMap<>();

	public void put(Class<? extends K> key, V value) {
		for (Class<?> cls : getSuperClasses(key)) {
			map.put(cls, value);
		}
	}

	public Set<V> get(Class<? extends K> key) {
		return map.get(key);
	}

	/**
	 * Return the provided class and all it's super classes and interfaces,
	 * recursively.
	 */
	private Set<Class<?>> getSuperClasses(Class<?> clazz) {
		Set<Class<?>> result = superClasses.get(clazz);
		if (result == null) {
			result = new HashSet<>();
			getSuperClasses(result, clazz);
			superClasses.put(clazz, result);
		}
		return result;
	}

	private void getSuperClasses(Set<Class<?>> result, Class<?> clazz) {
		if (clazz == null || !result.add(clazz)) {
			return;
		}
		getSuperClasses(result, clazz.getSuperclass());
		for (Class<?> i : clazz.getInterfaces()) {
			getSuperClasses(result, i);
		}
	}
}
