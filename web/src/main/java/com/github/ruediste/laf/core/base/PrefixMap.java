package com.github.ruediste.laf.core.base;

import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * A map from {@link String} to some value. The keys in the map are prefixes of
 * the keys which are beeing queried
 */
public class PrefixMap<V> {

	TreeMap<String, V> map = new TreeMap<>();

	public boolean contains(String query) {
		Entry<String, V> entry = map.floorEntry(query);
		return entry != null && query.startsWith(entry.getKey());
	}

	public V get(String query) {
		Entry<String, V> entry = map.floorEntry(query);
		if (entry != null && query.startsWith(entry.getKey())) {
			return entry.getValue();
		} else {
			return null;
		}
	}

	public Entry<String, V> getEntry(String query) {
		Entry<String, V> entry = map.floorEntry(query);
		if (entry != null && query.startsWith(entry.getKey())) {
			return entry;
		} else {
			return null;
		}
	}

	public void put(String prefix, V value) {
		if (contains(prefix)) {
			throw new IllegalArgumentException(
					"There is already a key in the map which is a prefix of the new key");
		}

		// check if there is a suffix in the map already
		{
			Entry<String, V> entry = map.ceilingEntry(prefix);
			if (entry != null && entry.getKey().startsWith(prefix)) {
				throw new IllegalArgumentException(
						"There is already a key in the map which is a suffix of the new key");
			}
		}
		map.put(prefix, value);
	}
}
