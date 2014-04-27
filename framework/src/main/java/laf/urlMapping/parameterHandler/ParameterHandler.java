package laf.urlMapping.parameterHandler;

import laf.attachedProperties.AttachedProperty;
import laf.controllerInfo.ParameterInfo;
import laf.urlMapping.UrlMappingRule;
import laf.urlMapping.parameterValueProvider.ParameterValueProvider;

/**
 * Maps parameters to URL strings. Used by {@link UrlMappingRule}s to handle
 * parameters.
 *
 * @see ParameterHandlerModule
 */
public interface ParameterHandler {

	/**
	 * Attached property for the parameter handler of a {@link ParameterInfo}
	 */
	static final AttachedProperty<ParameterHandler> parameterHandler = new AttachedProperty<>();

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
