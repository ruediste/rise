package com.github.ruediste.laf.core.httpRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.ruediste.laf.core.web.PathInfo;
import com.google.common.base.MoreObjects;

/**
 * Mutable implementation of the {@link HttpRequest} interface
 */
public class HttpRequestImpl extends HttpRequestBase {

	private final String pathInfo;
	private final HashMap<String, String[]> parameterMap = new HashMap<>();

	public HttpRequestImpl(PathInfo pathInfo) {
		this.pathInfo = pathInfo.getValue();
	}

	@Override
	public String getPathInfo() {
		return pathInfo;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return Collections.unmodifiableMap(parameterMap);
	}

	@Override
	public String getParameter(String name) {
		String[] result = parameterMap.get(name);
		if (result == null) {
			return null;
		}
		if (result.length == 0) {
			return null;
		}
		return result[0];
	}

	@Override
	public String[] getParameterValues(String name) {
		return parameterMap.get(name);
	}

	public Map<String, String[]> getModifiableParameterMap() {
		return parameterMap;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("path", pathInfo)
				.add("parameters", parameterMap).toString();
	}
}
