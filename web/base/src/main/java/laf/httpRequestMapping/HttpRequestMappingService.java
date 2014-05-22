package laf.httpRequestMapping;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.actionPath.ActionPath;
import laf.actionPath.ActionPath.ParameterValueComparator;
import laf.base.BaseModule;
import laf.base.BaseModule.ProjectStage;
import laf.configuration.ConfigValue;
import laf.controllerInfo.ControllerInfoRepository;
import laf.httpRequest.HttpRequest;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

@Singleton
public class HttpRequestMappingService {

	@Inject
	BaseModule baseModule;

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	final ArrayList<HttpRequestMappingRule> mappingRules = new ArrayList<>();

	@Inject
	@ConfigValue("laf.httpRequestMapping.defaultRule.DefaultHttpRequestParsingRuleFactory")
	Collection<HttpRequestMappingRuleFactory> mappingRuleFactories;

	@PostConstruct
	void initialize() {
		for (HttpRequestMappingRuleFactory factory : mappingRuleFactories) {
			mappingRules.addAll(factory.createRules());
		}

	}

	/**
	 * Parse a servlet path. If no matching rule is found, null is returned.
	 */
	public ActionPath<ParameterValueProvider> parse(HttpRequest request) {
		if (mappingRules.isEmpty()) {
			throw new RuntimeException(
					"No UrlMappingRules are defined in CoreConfig");
		}

		for (HttpRequestMappingRule rule : mappingRules) {
			ActionPath<ParameterValueProvider> result = rule.parse(request);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Generate a servlet path from an {@link ActionPath}.
	 */
	public HttpRequest generate(ActionPath<Object> path) {
		HttpRequest result = null;
		for (HttpRequestMappingRule rule : mappingRules) {
			result = rule.generate(path);
			if (result != null) {
				break;
			}
		}

		if (result == null) {
			throw new RuntimeException(
					"Error while generating URL. No rule matched the given action path "
							+ path);
		}

		// check if the generated URL can be parsed
		if (baseModule.getProjectStage() != ProjectStage.PRODUCTION) {

			ActionPath<ParameterValueProvider> parsed = parse(result);
			if (parsed == null) {
				throw new RuntimeException(
						"Cannot parse the generated url "
								+ result
								+ ". No rule matched. This is caused by an inconsistency between the URL generation and the URL parsing"
								+ " of the configured rules. Each rule has to parse exactly those URLs it generates.");
			}
			if (!path
					.isCallToSameActionMethod(
							parsed,
							new ParameterValueComparator<Object, ParameterValueProvider>() {

								@Override
								public boolean equals(Object a,
										ParameterValueProvider b) {
									return !b.providesNonEqualValue(a);
								}
							})) {
				throw new RuntimeException(
						"Parsing the generated URL "
								+ result
								+ " did not match the original ActionPath. This is caused by an inconsistency between the URL generation and the URL parsing"
								+ " of the configured rules. Each rule has to parse exactly those URLs it generates.");
			}
		}
		return result;
	}

}
