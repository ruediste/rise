package laf.httpRequestMapping.defaultRule;

import java.util.Collection;
import java.util.Collections;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.httpRequestMapping.HttpRequestMappingRule;
import laf.httpRequestMapping.twoStageMappingRule.DefaultActionPathSigner;
import laf.httpRequestMapping.twoStageMappingRule.TwoStageMappingRule;

public class DefaultHttpRequestMappingRuleFactory {

	@Inject
	DefaultHttpRequestMapper.Builder requestMapperBuilder;

	@Inject
	Instance<DefaultActionPathSigner> defaultActionPathSigner;

	@Inject
	Instance<DefaultParameterMapper> defaultParameterMapper;

	@Inject
	Instance<DefaultControllerIdentifierStrategy> strategy;

	public Collection<HttpRequestMappingRule> createRules() {
		return Collections
				.<HttpRequestMappingRule> singletonList(new TwoStageMappingRule(
						requestMapperBuilder.create(strategy.get()),
						defaultParameterMapper.get(), defaultActionPathSigner
								.get()));
	}

}
