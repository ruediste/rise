package com.github.ruediste.laf.core.httpRequest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;

/**
 * {@link HttpRequest} implementation delegating to a {@link HttpServletRequest}
 */
public class DelegatingHttpRequest extends HttpRequestBase {

	private HttpServletRequest delegate;

	public DelegatingHttpRequest(HttpServletRequest delegate) {
		this.delegate = delegate;
	}

	@Override
	public String getPathInfo() {
		return Strings.nullToEmpty(delegate.getPathInfo());
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return delegate.getParameterMap();
	}

	@Override
	public String getParameter(String name) {
		return delegate.getParameter(name);
	}

	@Override
	public String[] getParameterValues(String name) {
		return delegate.getParameterValues(name);
	}
}
