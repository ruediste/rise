package laf.httpRequestMapping;

import java.util.Collection;

/**
 * Factory which creates HttpRequestMappingRules
 */
public interface HttpRequestMappingRuleFactory {

	/**
	 * Create mapping rules.
	 */
	Collection<HttpRequestMappingRule> createRules();
}
