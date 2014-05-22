package sampleApp;

import java.util.*;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.actionPath.ActionPathFactory;
import laf.httpRequest.HttpRequest;
import laf.httpRequestMapping.HttpRequestMappingRule;
import laf.httpRequestMapping.HttpRequestMappingRuleFactory;
import laf.httpRequestMapping.defaultRule.DefaultHttpRequestMappingRuleFactory;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

public class MappingRuleFactory implements HttpRequestMappingRuleFactory {

	@Inject
	DefaultHttpRequestMappingRuleFactory defaultFactory;

	@Inject
	ActionPathFactory pathFactory;

	@Override
	public Collection<HttpRequestMappingRule> createRules() {
		List<HttpRequestMappingRule> result = new ArrayList<>();
		result.addAll(defaultFactory.createRules());
		result.add(new HttpRequestMappingRule() {

			@SuppressWarnings("unchecked")
			@Override
			public ActionPath<ParameterValueProvider> parse(HttpRequest request) {
				return (ActionPath<ParameterValueProvider>) pathFactory
						.buildActionPath(null)
						.controller(SampleComponentController.class).index();
			}

			@Override
			public HttpRequest generate(ActionPath<Object> path) {
				return null;
			}
		});
		return result;
	}
}
