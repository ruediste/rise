package laf.httpRequestMapping;

import javax.inject.Inject;
import javax.inject.Singleton;

import laf.actionPath.ActionPath;
import laf.actionPath.ActionPath.ParameterValueComparator;
import laf.base.BaseModule;
import laf.base.BaseModule.ProjectStage;
import laf.controllerInfo.ControllerInfoRepository;
import laf.httpRequest.HttpRequest;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

@Singleton
public class HttpRequestMappingService {

	@Inject
	BaseModule baseModule;

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	@Inject
	HttpRequestMappingRules mappingRules;

	/**
	 * Parse a servlet path. If no matching rule is found, null is returned.
	 */
	public ActionPath<ParameterValueProvider> parse(HttpRequest request) {
		if (mappingRules.get().isEmpty()) {
			throw new RuntimeException(
					"No UrlMappingRules are defined in CoreConfig");
		}

		for (HttpRequestMappingRule rule : mappingRules.get()) {
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
		for (HttpRequestMappingRule rule : mappingRules.get()) {
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
