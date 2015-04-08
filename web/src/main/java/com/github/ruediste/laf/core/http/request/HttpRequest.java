package com.github.ruediste.laf.core.http.request;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.github.ruediste.laf.core.base.attachedProperties.AttachedPropertyBearer;

/**
 * A reduced view of a {@link HttpServletRequest}
 */
public interface HttpRequest extends AttachedPropertyBearer {

	/**
	 * Return the path of this request. Initialized form
	 * {@link HttpServletRequest#getPathInfo()}
	 */
	String getPathInfo();

	/**
	 * @see HttpServletRequest#getParameterMap()
	 */
	Map<String, String[]> getParameterMap();

	/**
	 * @see HttpServletRequest#getParameter(String)
	 */
	String getParameter(String name);

	/**
	 * @see HttpServletRequest#getParameterValues(String)
	 */
	String[] getParameterValues(String name);

	/**
	 * Check for equality of the {@link #getPathInfo()} and the
	 * {@link #getParameterMap()}
	 */
	@Override
	public boolean equals(Object obj);

	/**
	 * Return {@link #getPathInfo()} with the parameters appended
	 */
	String getPathWithParameters();
}
