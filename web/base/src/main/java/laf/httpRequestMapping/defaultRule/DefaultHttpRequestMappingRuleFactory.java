package laf.httpRequestMapping.defaultRule;

import java.util.Collection;
import java.util.Collections;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.httpRequestMapping.HttpRequestMappingRule;
import laf.httpRequestMapping.HttpRequestMappingRuleFactory;
import laf.httpRequestMapping.twoStageMappingRule.DefaultActionPathSigner;
import laf.httpRequestMapping.twoStageMappingRule.TwoStageMappingRule;

public class DefaultHttpRequestMappingRuleFactory implements
HttpRequestMappingRuleFactory {

	@Inject
	DefaultHttpRequestMapper.Builder requestMapperBuilder;

	@Inject
	Instance<DefaultActionPathSigner> defaultActionPathSigner;

	@Inject
	Instance<DefaultParameterMapper> defaultParameterMapper;

	@Inject
	Instance<DefaultControllerIdentifierStrategy> strategy;

	@Override
	public Collection<HttpRequestMappingRule> createRules() {
		return Collections
				.<HttpRequestMappingRule> singletonList(new TwoStageMappingRule(
						requestMapperBuilder.create(strategy.get()),
						defaultParameterMapper.get(), defaultActionPathSigner
						.get()));
	}

}
