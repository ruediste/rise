package com.github.ruediste.laf.core.httpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.MoreObjects;

/**
 * Mutable implementation of the {@link HttpRequest} interface
 */
public class HttpRequestImpl extends HttpRequestBase {

	private String pathInfo;
	private HashMap<String, String[]> parameterMap = new HashMap<>();

	public HttpRequestImpl() {
	}

	/**
	 * Create a new instance. The provided pathInfo is decoded (
	 * {@link URLDecoder#decode(String, String)})
	 * 
	 * @param pathInfo
	 */
	public HttpRequestImpl(String pathInfo) {
		try {
			this.pathInfo = URLDecoder.decode(pathInfo, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
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

	public void setPath(String path) {
		this.pathInfo = path;
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
