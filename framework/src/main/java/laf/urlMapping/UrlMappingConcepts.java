package laf.urlMapping;

import javax.persistence.EntityManager;

import laf.LAF;
import laf.controllerInfo.ParameterInfo;

/**
 * URLs have to be mapped to action method invocations for request handling, and
 * action method invocations have to be mapped to URLs for URL generation.
 *
 * <p>
 * During this round trip, different representations of the parameter values are
 * necessary. When parsing an URL, entity parameters may not be loaded from the
 * database, since the controller and the action method have an influence on the
 * transaction and {@link EntityManager} which is to be used. Thus parameters
 * are represented as {@link ParameterValueProvider}s after parsing.
 * </p>
 *
 * <p>
 * When representing an invocation to an action method, the parameters are at
 * first represented by original parameter objects, get transformed to strings
 * afterwards and are finally combined to the URL.
 * </p>
 *
 * <p>
 * A list of {@link UrlMappingRule}s is used to perform the actual mapping,
 * allowing total customization thereof. The list is configured via
 * {@link LAF#getUrlMappingRules()}
 * </p>
 */
public class UrlMappingConcepts {

	/**
	 * <p>
	 * In many cases the mapping of URLs can be split in two aspects: There is
	 * the overall format of the URL and the way individual parameters are
	 * represented. While the configuration of the mapping rules gives you
	 * complete freedom of the mapping, we implemented an infrastructure for
	 * parameter value handling. This infrastructure is used by the default
	 * {@link UrlMappingRule}s, but can as well be used by your own
	 * customizations.
	 * </p>
	 *
	 * <p>
	 * A {@link ParameterHandler} is used to perform the mapping of a parameter.
	 * The handlers are configured via {@link LAF#getParameterHandlers()}.
	 * During initialization, the first matching handler for each
	 * {@link ParameterInfo} is determined and stored in {@link
	 * DefaultUrlMappingRule#} {@link ParameterInfo#getParameterHandler()}.
	 * </p>
	 */
	public void parameterHandlers() {

	}
}
