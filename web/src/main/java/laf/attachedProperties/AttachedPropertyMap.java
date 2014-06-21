package laf.attachedProperties;

import java.util.HashMap;

/**
 * A map for attached properties. Instances of this class can be constructed and
 * managed by classes of other modules, but the methods are not accessible,
 * since they are intended to be used through {@link AttachedProperty}
 * instances.
 *
 * <p>
 * All operations are thread safe.
 * </p>
 */
public class AttachedPropertyMap {

	private HashMap<AttachedProperty<?, ?>, Object> map = new HashMap<>();

	@SuppressWarnings("unchecked")
	synchronized <T> T get(AttachedProperty<?, T> key) {
		return (T) map.get(key);
	}

	synchronized <T> void set(AttachedProperty<?, T> key, T value) {
		map.put(key, value);
	}

	synchronized void clear(AttachedProperty<?, ?> key) {
		map.remove(key);
	}

	synchronized boolean isSet(AttachedProperty<?, ?> key) {
		return map.containsKey(key);
	}

}
