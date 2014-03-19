package laf.attachedProperties;

import java.util.HashMap;

/**
 * A map for attached properties. All operations are synchronized on this.
 */
public class AttachedPropertyMap {

	private HashMap<AttachedPropertyKey<?>, Object> map = new HashMap<>();

	@SuppressWarnings("unchecked")
	synchronized public <T> T getAttachedProperty(AttachedPropertyKey<T> key) {
		return (T) map.get(key);
	}

	synchronized public <T> void set(
			AttachedPropertyKey<T> key, T value) {
		map.put(key, value);
	}

	synchronized public void clear(AttachedPropertyKey<?> key) {
		map.remove(key);
	}

	synchronized public boolean isSet(AttachedPropertyKey<?> key) {

		return map.containsKey(key);
	}

}
