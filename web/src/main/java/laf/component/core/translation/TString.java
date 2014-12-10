package laf.component.core.translation;

import java.util.*;

import laf.core.base.Val;

import com.google.common.base.Objects;

/**
 * A translated string.
 *
 * <p>
 * A {@link TString} represents a string in all possible translations. It
 * contains a resource key and all parameters which are passed to the template
 * which is looked up by the resource key.
 * </p>
 */
public class TString {
	final private String resourceKey;
	private final Map<String, Object> parameters;

	public TString(String resourceKey, Map<String, Object> parameters) {
		this.resourceKey = resourceKey;
		this.parameters = new HashMap<>(parameters);
	}

	/**
	 * Construct a {@link TString} using a key and a parameter list. The
	 * parameters are pairs of parameter names and parameter values
	 */
	public TString(String resourceKey, Object... parameters) {
		this.resourceKey = resourceKey;
		this.parameters = new HashMap<>();

		if ((parameters.length % 2) != 0) {
			throw new IllegalArgumentException(
					"The number of parameters has to be even (key-value pairs)");
		}
		for (int i = 0; i < parameters.length; i += 2) {
			Object key = parameters[i];
			Object value = parameters[i + 1];
			if (!(key instanceof String)) {
				throw new IllegalArgumentException("index " + i
						+ ": parameter keys have to be strings");
			}
			this.parameters.put((String) key, value);
		}
	}

	public String getResourceKey() {
		return resourceKey;
	}

	public Map<String, Object> getParameters() {
		return Collections.unmodifiableMap(parameters);
	}

	public Val<Object> getParameter(String key) {
		if (parameters.containsKey(key)) {
			return Val.of(parameters.get(key));
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("resourceKey", resourceKey)
				.add("parameters", parameters).toString();
	}
}
