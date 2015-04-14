package com.github.ruediste.laf.core.httpRequest;

import java.util.*;

import com.google.common.base.Objects;

/**
 * Mutable implementation of the {@link HttpRequest} interface
 */
public class HttpRequestImpl extends HttpRequestBase {

	String path;
	HashMap<String, String[]> parameterMap = new HashMap<>();

	public HttpRequestImpl() {
	}

	public HttpRequestImpl(String path) {
		this.path = path;
	}

	@Override
	public String getPathInfo() {
		return path;
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

	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, String[]> getModifiableParameterMap() {
		return parameterMap;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("path", path)
				.add("parameters", parameterMap).toString();
	}
}
