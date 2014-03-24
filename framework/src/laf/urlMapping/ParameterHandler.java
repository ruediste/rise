package laf.urlMapping;

import laf.controllerInfo.ParameterInfo;

/**
 * Maps parameters to URL strings. Used by {@link UrlMappingRule}s to handle
 * parameters.
 * 
 * @see UrlMappingConcepts#parameterHandlers()
 */
public interface ParameterHandler {

	/**
	 * Returns true if this handler can handle the specified parameter.
	 */
	boolean handles(ParameterInfo info);

	/**
	 * Generate a string representation which can be parsed later
	 */
	String generate(ParameterInfo info, Object value);

	/**
	 * Parse the string representation of the parameter.
	 */
	ParameterValueProvider parse(ParameterInfo info, String urlPart);
}
