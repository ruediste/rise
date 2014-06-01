package sampleApp;

import java.util.*;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.actionPath.ActionPathFactory;
import laf.http.request.HttpRequest;
import laf.http.requestMapping.HttpRequestMappingRule;
import laf.http.requestMapping.defaultRule.DefaultHttpRequestMappingRuleFactory;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;

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
