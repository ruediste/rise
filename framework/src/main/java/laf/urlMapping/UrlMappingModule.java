package laf.urlMapping;

import java.util.ArrayDeque;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import laf.LAF;
import laf.LAF.ProjectStage;
import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.actionPath.ActionPath.ParameterValueComparator;
import laf.actionPath.ActionPathModule;
import laf.configuration.ConfigurationModule;
import laf.configuration.ConfigurationParameter;
import laf.controllerInfo.ControllerInfoModule;
import laf.initialization.InitializationModule;
import laf.urlMapping.parameterValueProvider.ParameterValueProvider;
import laf.urlMapping.parameterValueProvider.ParameterValueProviderModule;

import org.jabsaw.Module;

/**
 * URLs have to be mapped to action method invocations for request handling, and
 * action method invocations have to be mapped to URLs for URL generation.
 *
 * <p>
 * During this round trip, different representations of the parameter values are
 * necessary. When parsing an URL, entity parameters may not be loaded from the
 * database, since the controller and the action method have an influence on the
 * transaction and {@link EntityManager} which is to be used. Thus parameters
 * are represented as {@link ParameterValueProvider}s after parsing.
 * </p>
 *
 * <p>
 * When representing an invocation to an action method, the parameters are at
 * first represented by original parameter objects, get transformed to strings
 * afterwards and are finally combined to the URL.
 * </p>
 *
 * <p>
 * A list of {@link UrlMappingRule}s is used to perform the actual mapping,
 * allowing total customization thereof. The list is configured via
 * {@link LAF#getUrlMappingRules()}
 * </p>
 */
@Module(description = "Maps URLs to ActionPaths using UrlMappingRules", exported = {
		ActionPathModule.class, ControllerInfoModule.class,
		ParameterValueProviderModule.class }, imported = {
		InitializationModule.class, ConfigurationModule.class })
@Singleton
public class UrlMappingModule {

	@Inject
	LAF laf;

	public ConfigurationParameter<ArrayDeque<UrlMappingRule>> urlMappingRules = new ConfigurationParameter<ArrayDeque<UrlMappingRule>>(
			new ArrayDeque<UrlMappingRule>());

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
