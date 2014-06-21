package laf.http.requestMapping.parameterHandler;

import laf.controllerInfo.ParameterInfo;
import laf.http.requestMapping.HttpRequestMappingRule;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;

/**
 * Maps parameters to URL strings. Used by {@link HttpRequestMappingRule}s to
 * handle parameters.
 *
 * @see ParameterHandlerModule
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
