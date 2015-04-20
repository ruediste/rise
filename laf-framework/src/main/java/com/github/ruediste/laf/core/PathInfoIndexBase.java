package com.github.ruediste.laf.core;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.laf.util.Pair;

/**
 * Store path infos and path info prefixes.
 *
 * <p>
 * This map is used to locate controllers based on their registered path infos
 * or prefixes.
 */
public class PathInfoIndexBase<T> {

	@Inject
	Logger log;

	private HashMap<String, T> pathInfoMap = new HashMap<>();

	TreeMap<String, T> prefixMap = new TreeMap<>();

	public void registerPathInfo(String pathInfo, T handler) {
		T existing = pathInfoMap.put(pathInfo, handler);
		if (existing != null) {
			throw new IllegalArgumentException(
					"Registered duplicate path info:\n" + pathInfo + "\n"
							+ existing + "\n" + handler);
		}
		log.debug("registered exact " + pathInfo);
	}

	public T getHandler(String pathInfo) {
		Pair<String, T> pair = getPrefixAndHandler(pathInfo);
		if (pair == null)
			return null;
		return pair.getB();
	}

	/**
	 * Return the prefix and the handler or null, if no handler has been found.
	 */
	public Pair<String, T> getPrefixAndHandler(String pathInfo) {
		// check map
		{
			T result = pathInfoMap.get(pathInfo);
			if (result != null)
				return Pair.of(pathInfo, result);
		}

		// check prefix map
		{
			Entry<String, T> entry = prefixMap.floorEntry(pathInfo);
			if (entry != null && pathInfo.startsWith(entry.getKey())) {
				return Pair.of(entry.getKey(), entry.getValue());
			}
		}

		// nothing found
		return null;
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
		log.debug("registered prefix " + prefix);
	}
}
