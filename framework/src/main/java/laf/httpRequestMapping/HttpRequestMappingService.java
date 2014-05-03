package laf.httpRequestMapping;

import java.util.Deque;
import java.util.LinkedList;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.actionPath.ActionPath.ParameterValueComparator;
import laf.base.BaseModule;
import laf.base.BaseModule.ProjectStage;
import laf.httpRequest.HttpRequest;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

import com.google.common.base.Function;

public class HttpRequestMappingService {
	@Inject
	HttpRequestMappingModule httpRequestMappingModule;

	@Inject
	BaseModule baseModule;

	private final Deque<HttpRequestMappingRule> mappingRules = new LinkedList<>();

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

	/**
	 * Convert an {@link ActionPath} with {@link ParameterValueProvider}s to an
	 * ActionPath with {@link Object}s, using the
	 * {@link ParameterValueProvider#provideValue()}
	 */
	public static ActionPath<Object> createObjectActionPath(
			ActionPath<ParameterValueProvider> actionPath) {
		return actionPath.map(new Function<ParameterValueProvider, Object>() {

			@Override
			public Object apply(ParameterValueProvider input) {
				return input.provideValue();
			}
		});
	}

	public Deque<HttpRequestMappingRule> getMappingRules() {
		return mappingRules;
	}

}
