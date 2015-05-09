package com.github.ruediste.rise.core.web;

import javax.servlet.http.HttpServletRequest;

/**
 * Wrapps a string representing a {@link HttpServletRequest#getPathInfo()
 * pathInfo}
 */
public class PathInfo {

	private final String value;

	public PathInfo(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}
}
