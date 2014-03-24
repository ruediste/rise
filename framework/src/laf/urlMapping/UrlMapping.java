package laf.urlMapping;

import javax.inject.Inject;
import javax.inject.Singleton;

import laf.LAF;
import laf.LAF.ProjectStage;
import laf.urlMapping.ActionPath.ParameterValueComparator;

@Singleton
public class UrlMapping {

	@Inject
	LAF coreConfig;

	/**
	 * Parse a servlet path. If no matching rule is found, null is returned.
	 */
	ActionPath<ParameterValueProvider> parse(String servletPath) {
		for (UrlMappingRule rule : coreConfig.getUrlMappingRules()) {
			ActionPath<ParameterValueProvider> result = rule.parse(servletPath);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Generate a servlet path from an {@link ActionPath}.
	 */
	String generate(ActionPath<Object> path) {
		String result = null;
		for (UrlMappingRule rule : coreConfig.getUrlMappingRules()) {
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
		if (coreConfig.getProjectStage() != ProjectStage.PRODUCTION) {
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
