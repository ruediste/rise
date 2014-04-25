package laf.urlMapping;

import java.util.ArrayDeque;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.LAF;
import laf.LAF.ProjectStage;
import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.actionPath.ActionPath.ParameterValueComparator;
import laf.configuration.ConfigurationParameter;
import laf.urlMapping.defaultRule.DefaultUrlMappingRule;
import laf.urlMapping.parameterValueProvider.ParameterValueProvider;

@Singleton
public class UrlMapping {

	LAF laf;

	public ConfigurationParameter<ArrayDeque<UrlMappingRule>> urlMappingRules = new ConfigurationParameter<ArrayDeque<UrlMappingRule>>(
			new ArrayDeque<UrlMappingRule>());

	@Inject
	Instance<UrlMappingRule> ruleInstance;

	@PostConstruct
	public void initialize() {
		urlMappingRules.getValue().add(
				ruleInstance.select(DefaultUrlMappingRule.class).get());
	}

	/**
	 * Parse a servlet path. If no matching rule is found, null is returned.
	 */
	public ActionPath<ParameterValueProvider> parse(String servletPath) {
		if (urlMappingRules.getValue().isEmpty()) {
			throw new RuntimeException(
					"No UrlMappingRules are defined in CoreConfig");
		}

		for (UrlMappingRule rule : urlMappingRules.getValue()) {
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
	public String generate(ActionPath<Object> path) {
		String result = null;
		for (UrlMappingRule rule : urlMappingRules.getValue()) {
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
		if (laf.getProjectStage() != ProjectStage.PRODUCTION) {
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

	/**
	 * Convert an {@link ActionPath} with {@link ParameterValueProvider}s to an
	 * ActionPath with {@link Object}s, using the
	 * {@link ParameterValueProvider#provideValue()}
	 */
	public static ActionPath<Object> createObjectActionPath(
			ActionPath<ParameterValueProvider> actionPath) {
		ActionPath<Object> result = new ActionPath<Object>();
		for (ActionInvocation<ParameterValueProvider> invocation : actionPath
				.getElements()) {
			ActionInvocation<Object> i = new ActionInvocation<Object>(
					invocation);
			for (ParameterValueProvider provider : invocation.getArguments()) {
				i.getArguments().add(provider.provideValue());
			}
			result.getElements().add(i);
		}
		return result;
	}
}
