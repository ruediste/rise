package laf.httpRequestMapping.twoStageMappingRule;

import laf.actionPath.ActionPath;
import laf.httpRequest.HttpRequest;

/**
 * Strategy class to map between {@link HttpRequest}s and string
 * {@link ActionPath}s.
 */
public interface HttpRequestMapper {

	/**
	 * Parse a {@link HttpRequest}. If null is returned,
	 */
	ActionPath<String> parse(HttpRequest request);

	HttpRequest generate(ActionPath<String> path);

	boolean handles(ActionPath<Object> path);

}
