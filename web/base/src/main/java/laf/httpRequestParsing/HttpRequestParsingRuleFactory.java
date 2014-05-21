package laf.httpRequestParsing;

import java.util.Collection;

/**
 * Factory which creates HttpRequestMappingRules
 */
public interface HttpRequestParsingRuleFactory {

	/**
	 * Create mapping rules.
	 */
	Collection<HttpRequestParsingRule> createRules();
}
