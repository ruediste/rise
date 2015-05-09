package com.github.ruediste.rise.core.front;

import javax.servlet.http.HttpServletRequest;

public enum HttpMethod {

	DELETE, HEAD, GET, OPTIONS, POST, PUT, TRACE;

	/**
	 * Return the value based on {@link HttpServletRequest#getMethod()}
	 */
	public static HttpMethod get(String method){
		return valueOf(method);
	}
}
