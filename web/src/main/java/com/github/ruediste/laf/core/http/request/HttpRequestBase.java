package com.github.ruediste.laf.core.http.request;

import java.util.*;
import java.util.Map.Entry;

import com.github.ruediste.laf.core.base.attachedProperties.AttachedPropertyBearerBase;
import com.google.common.base.Joiner;

/**
 * Base implementation of {@link HttpRequest}, providing the
 * {@link #equals(Object)} implementation.
 */
public abstract class HttpRequestBase extends AttachedPropertyBearerBase
implements HttpRequest {

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HttpRequest)) {
			return false;
		}
		HttpRequest other = (HttpRequest) obj;

		return Objects.equals(getPathInfo(), other.getPathInfo())
				&& Objects.equals(getParameterMap(), other.getParameterMap());
	}

	@Override
	public String getPathWithParameters() {
		if (getParameterMap().isEmpty()) {
			return getPathInfo();
		}
		ArrayList<String> parameters = new ArrayList<>();
		for (Entry<String, String[]> entry : getParameterMap().entrySet()) {
			for (String value : entry.getValue()) {
				parameters.add(entry.getKey() + "=" + value);
			}
		}

		return getPathInfo() + "?" + Joiner.on("&").join(parameters);
	}

}