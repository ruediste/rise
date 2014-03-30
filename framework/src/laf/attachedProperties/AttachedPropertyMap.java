package laf.attachedProperties;

import java.util.HashMap;

/**
 * A map for attached properties. All operations are synchronized on this.
 */
public class AttachedPropertyMap {

	private HashMap<AttachedProperty<?>, Object> map = new HashMap<>();

	@SuppressWarnings("unchecked")
	synchronized public <T> T getAttachedProperty(AttachedProperty<T> key) {
		return (T) map.get(key);
	}

	synchronized public <T> void set(
			AttachedProperty<T> key, T value) {
		map.put(key, value);
	}

	synchronized public void clear(AttachedProperty<?> key) {
		map.remove(key);
	}

	synchronized public boolean isSet(AttachedProperty<?> key) {

		return map.containsKey(key);
	}

}
