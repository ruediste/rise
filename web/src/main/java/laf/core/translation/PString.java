package laf.core.translation;

import java.util.*;

import laf.core.base.Val;

import com.google.common.base.Objects;

/**
 * A translated, pattern based string.
 *
 * <p>
 * Represents a string in all possible languages. The pattern is represented as
 * {@link TString}. After resolving the pattern, the contained placeholders are
 * substituted with the corresponding parameters.
 * </p>
 */
public class PString {
	private final TString pattern;
	private final Map<String, Object> parameters;

	public PString(TString pattern, Map<String, Object> parameters) {
		this.pattern = pattern;
		this.parameters = new HashMap<>(parameters);
	}

	/**
	 * Construct a {@link TPString} using a key and a parameter list. The
	 * parameters are pairs of parameter names and parameter values
	 */
	public PString(TString pattern, Object... parameters) {
		this.pattern = pattern;
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
		return Objects.toStringHelper(this).add("template", pattern)
				.add("parameters", parameters).toString();
	}

	public TString getPattern() {
		return pattern;
	}
}
