package laf.httpRequest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * A reduced representation of a {@link HttpServletRequest}
 */
public interface HttpRequest {

	/**
	 * Return the path of this request. Initialized form
	 * {@link HttpServletRequest#getServletPath()}
	 */
	String getPath();

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
	 * Check for equality of the {@link #getPath()} and the
	 * {@link #getParameterMap()}
	 */
	@Override
	public boolean equals(Object obj);
}
