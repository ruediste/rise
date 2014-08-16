package laf.core.http.requestMapping;

import javax.inject.Inject;
import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.base.BaseModule.ProjectStage;
import laf.base.configuration.ConfigurationValue;
import laf.core.controllerInfo.ControllerInfoRepository;
import laf.core.http.request.HttpRequest;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.mvc.actionPath.ActionPath;
import laf.mvc.actionPath.ActionPath.ParameterValueComparator;

@Singleton
public class HttpRequestMappingService {

	@Inject
	BaseModule baseModule;

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	@Inject
	ConfigurationValue<HttpRequestMappingRules> mappingRules;

	/**
	 * Parse a servlet path. If no matching rule is found, null is returned.
	 */
	public ActionPath<ParameterValueProvider> parse(HttpRequest request) {
		if (mappingRules.value().get().isEmpty()) {
			throw new RuntimeException(
					"No UrlMappingRules are defined in the configuration");
		}

		for (HttpRequestMappingRule rule : mappingRules.value().get()) {
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
		for (HttpRequestMappingRule rule : mappingRules.value().get()) {
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
