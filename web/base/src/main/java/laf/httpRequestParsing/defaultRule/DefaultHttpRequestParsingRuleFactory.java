package laf.httpRequestParsing.defaultRule;

import java.util.Collection;
import java.util.Collections;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.httpRequestParsing.HttpRequestParsingRule;
import laf.httpRequestParsing.HttpRequestParsingRuleFactory;
import laf.httpRequestParsing.twoStageMappingRule.DefaultActionPathSigner;
import laf.httpRequestParsing.twoStageMappingRule.TwoStageMappingRule;

public class DefaultHttpRequestParsingRuleFactory implements
HttpRequestParsingRuleFactory {

	@Inject
	DefaultHttpRequestParser.Builder requestMapperBuilder;

	@Inject
	Instance<DefaultActionPathSigner> defaultActionPathSigner;

	@Inject
	Instance<DefaultParameterMapper> defaultParameterMapper;

	@Inject
	Instance<DefaultControllerIdentifierStrategy> strategy;

	@Override
	public Collection<HttpRequestParsingRule> createRules() {
		return Collections
				.<HttpRequestParsingRule> singletonList(new TwoStageMappingRule(
						requestMapperBuilder.create(strategy.get()),
						defaultParameterMapper.get(), defaultActionPathSigner
						.get()));
	}

}
