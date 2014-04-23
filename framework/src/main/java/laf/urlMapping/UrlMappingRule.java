package laf.urlMapping;

import laf.actionPath.ActionPath;
import laf.urlMapping.parameterHandler.ParameterValueProvider;


/**
 * {@link UrlMappingRule}s map URLs to {@link ActionPath}s. There is a system
 * wide list of rules which is tried one after the other, until one of them
 * returns a non-null value. It is not possible to specify per controller or per
 * action method rules. However, an extension could add a rule which processes
 * some controller and action method meta data, and performs a mapping based on
 * it.
 */
public interface UrlMappingRule {
	/**
	 * Parse a servlet path. If null is returned the next rule is tried.
	 */
	ActionPath<ParameterValueProvider> parse(String servletPath);

	/**
	 * Generate a servlet path from an {@link ActionPath}. If null is returned,
	 * the next rule is tried.
	 */
	String generate(ActionPath<Object> path);

}
