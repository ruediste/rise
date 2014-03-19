package laf.urlMapping;

import javax.persistence.EntityManager;

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
 * allowing total customization thereof.
 * </p>
 */
public class UrlMappingConcepts {

}
