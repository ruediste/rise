package laf.core.http.requestMapping;

import javax.servlet.http.HttpServletRequest;

import laf.core.actionPath.ActionPath;
import laf.core.http.request.HttpRequest;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;

/**
 * {@link HttpRequestMappingRule}s map {@link HttpServletRequest}s to
 * {@link ActionPath}s. There is a system wide list of rules which is tried one
 * after the other, until one of them returns a non-null value. It is not
 * possible to specify per controller or per action method rules. However, an
 * extension could add a rule which processes some controller and action method
 * meta data, and performs a mapping based on it.
 *
 * <p>
 * The rules are responsible for url signing.
 * </p>
 */
public interface HttpRequestMappingRule {
	/**
	 * Parse a {@link HttpRequest} path. If null is returned the next rule is
	 * tried.
	 */
	ActionPath<ParameterValueProvider> parse(HttpRequest request);

	/**
	 * Generate an URL from an {@link ActionPath}. If null is returned, the next
	 * rule is tried.
	 */
	HttpRequest generate(ActionPath<Object> path);
}
