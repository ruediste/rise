package sampleApp;

import java.util.*;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.actionPath.ActionPathFactory;
import laf.httpRequest.HttpRequest;
import laf.httpRequest.HttpRequestImpl;
import laf.httpRequestParsing.HttpRequestParsingRule;
import laf.httpRequestParsing.HttpRequestParsingRuleFactory;
import laf.httpRequestParsing.defaultRule.DefaultHttpRequestParsingRuleFactory;
import laf.httpRequestParsing.parameterValueProvider.ParameterValueProvider;

public class MappingRuleFactory implements HttpRequestParsingRuleFactory {

	@Inject
	DefaultHttpRequestParsingRuleFactory defaultFactory;

	@Inject
	ActionPathFactory pathFactory;

	@Override
	public Collection<HttpRequestParsingRule> createRules() {
		List<HttpRequestParsingRule> result = new ArrayList<>();
		result.addAll(defaultFactory.createRules());
		result.add(new HttpRequestParsingRule() {

			@SuppressWarnings("unchecked")
			@Override
			public ActionPath<ParameterValueProvider> parse(HttpRequest request) {
				return (ActionPath<ParameterValueProvider>) pathFactory
						.buildActionPath(null)
						.controller(SampleComponentController.class).index();
			}

			@Override
			public HttpRequest generate(ActionPath<Object> path) {
				return new HttpRequestImpl("/");
			}
		});
		return result;
	}
}
