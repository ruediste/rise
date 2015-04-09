package com.github.ruediste.laf.core.requestParserChain;

import java.util.*;
import java.util.Map.Entry;

/**
 * Store path infos and path info prefixes.
 *
 * <p>
 * This map is used to locate controllers based on their registered path infos
 * or prefixes.
 */
public class PathInfoIndexBase<T> {

	private HashMap<String, T> pathInfoMap = new HashMap<>();

	TreeMap<String, T> prefixMap = new TreeMap<>();

	public void registerPathInfo(String pathInfo, T handler) {
		T existing = pathInfoMap.put(pathInfo, handler);
		if (existing != null) {
			throw new IllegalArgumentException(
					"Registered duplicate path info:\n" + pathInfo + "\n"
							+ existing + "\n" + handler);
		}
	}

	public T getHandler(String pathInfo) {
		T result = pathInfoMap.get(pathInfo);
		if (result == null) {
			Entry<String, T> entry = prefixMap.floorEntry(pathInfo);
			if (entry != null && pathInfo.startsWith(entry.getKey())) {
				result = entry.getValue();
			}
		}
		return result;
	}

	public void registerPrefix(String prefix, T handler) {
		Entry<String, T> floorEntry = prefixMap.floorEntry(prefix);
		Entry<String, T> ceilingEntry = prefixMap.ceilingEntry(prefix);

		if (floorEntry != null
				&& (floorEntry.getKey().startsWith(prefix) || prefix
						.startsWith(floorEntry.getKey()))) {
			throw new IllegalArgumentException(
					"Attempt to register ambigous prefix:\n" + prefix + "->"
							+ handler + "\n" + floorEntry.getKey() + "->"
							+ floorEntry.getValue());
		}
		if (ceilingEntry != null
				&& (ceilingEntry.getKey().startsWith(prefix) || prefix
						.startsWith(ceilingEntry.getKey()))) {
			throw new IllegalArgumentException(
					"Attempt to register ambigous prefix:\n" + prefix + "->"
							+ handler + "\n" + ceilingEntry.getKey() + "->"
							+ ceilingEntry.getValue());
		}

		prefixMap.put(prefix, handler);
	}
}
