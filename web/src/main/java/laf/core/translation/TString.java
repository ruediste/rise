package laf.core.translation;

import com.google.common.base.Objects;

/**
 * A translated string.
 *
 * <p>
 * A {@link TString} represents a string in all possible languages. It contains
 * a resource key and a fallback. When resolving the string in a given locale,
 * the resources for the locale are searched for the resourceKey. If no resource
 * is found, the fallback is used (if not null).
 * </p>
 */
public class TString {
	final private String resourceKey;
	private final String fallback;

	public TString(String resourceKey) {
		this(resourceKey, null);
	}

	public TString(String resourceKey, String fallback) {
		this.resourceKey = resourceKey;
		this.fallback = fallback;
	}

	public String getResourceKey() {
		return resourceKey;
	}

	public String getFallback() {
		return fallback;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("resourceKey", resourceKey)
				.add("fallback", fallback).toString();
	}
}
