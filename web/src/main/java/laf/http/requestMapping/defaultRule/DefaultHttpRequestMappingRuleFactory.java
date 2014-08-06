package laf.http.requestMapping.defaultRule;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.http.requestMapping.HttpRequestMappingRule;
import laf.http.requestMapping.twoStageMappingRule.DefaultActionPathSigner;
import laf.http.requestMapping.twoStageMappingRule.TwoStageMappingRule;

public class DefaultHttpRequestMappingRuleFactory {

	@Inject
	Instance<DefaultHttpRequestMapper> requestMapperInstance;

	@Inject
	Instance<DefaultActionPathSigner> defaultActionPathSigner;

	@Inject
	Instance<DefaultParameterMapper> defaultParameterMapper;

	@Inject
	Instance<DefaultControllerIdentifierStrategy> strategy;

	public Deque<HttpRequestMappingRule> createRules() {
		ArrayDeque<HttpRequestMappingRule> result = new ArrayDeque<HttpRequestMappingRule>();
		DefaultHttpRequestMapper requestMapper = requestMapperInstance.get();
		requestMapper.initialize(strategy.get());
		result.add(new TwoStageMappingRule(requestMapper, defaultParameterMapper.get(), defaultActionPathSigner
				.get()));
		return result;
	}

}
