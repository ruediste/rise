package laf.core.http.request;

import java.util.*;
import java.util.Map.Entry;

import laf.base.attachedProperties.AttachedPropertyBearerBase;

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

		return Objects.equals(getPath(), other.getPath())
				&& Objects.equals(getParameterMap(), other.getParameterMap());
	}

	@Override
	public String getPathWithParameters() {
		if (getParameterMap().isEmpty()) {
			return getPath();
		}
		ArrayList<String> parameters = new ArrayList<>();
		for (Entry<String, String[]> entry : getParameterMap().entrySet()) {
			for (String value : entry.getValue()) {
				parameters.add(entry.getKey() + "=" + value);
			}
		}

		return getPath() + "?" + Joiner.on("&").join(parameters);
	}

}