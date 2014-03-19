package laf.urlMapping;

import laf.controllerInfo.ActionMethodInfo;
import laf.controllerInfo.ControllerInfo;

/**
 * {@link UrlMappingRule}s map URLs to {@link ActionPath}s. There is a system
 * wide list of rules which is tried one after the other, until one of them
 * returns a non-null value. In addition, each controller and each action method
 * can specify additional rules, which are tried before the system wide rules.
 * (Rules specified by action methods are tried before the rules defined by the
 * controller). The mechanism to add rules to controllers and action methods is
 * not defined, but they can be added to the {@link ControllerInfo} and
 * {@link ActionMethodInfo} instances.
 * 
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
