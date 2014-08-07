package sampleApp;

import java.util.*;

import javax.inject.Inject;

import laf.core.actionPath.ActionPath;
import laf.core.actionPath.ActionPathFactory;
import laf.core.http.request.HttpRequest;
import laf.core.http.requestMapping.HttpRequestMappingRule;
import laf.core.http.requestMapping.defaultRule.DefaultHttpRequestMappingRuleFactory;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public class MappingRuleFactory {

	@Inject
	DefaultHttpRequestMappingRuleFactory defaultFactory;

	@Inject
	ActionPathFactory pathFactory;

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
