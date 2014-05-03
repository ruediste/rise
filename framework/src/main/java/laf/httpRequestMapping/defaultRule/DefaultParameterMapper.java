package laf.httpRequestMapping.defaultRule;

import laf.actionPath.ActionPath;
import laf.base.Function2;
import laf.controllerInfo.ParameterInfo;
import laf.httpRequestMapping.parameterHandler.*;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;
import laf.httpRequestMapping.twoStageMappingRule.ParameterMapper;
import laf.initialization.LafInitializer;

/**
 * A {@link ParameterMapper} using the {@link ParameterHandlerModule}
 * infrastructure.
 */
public class DefaultParameterMapper implements ParameterMapper {

	@LafInitializer(after = ParameterHandlerInitializer.class)
	public void initialize() {

	}

	@Override
	public ActionPath<ParameterValueProvider> parse(
			final ActionPath<String> path) {
		return path
				.mapWithParameter(new Function2<ParameterInfo, String, ParameterValueProvider>() {

					@Override
					public ParameterValueProvider apply(ParameterInfo a,
							String b) {
						return ParameterHandler.parameterHandler.get(a).parse(
								a, b);
					}
				});
	}

	@Override
	public ActionPath<String> generate(final ActionPath<Object> path) {
		return path
				.mapWithParameter(new Function2<ParameterInfo, Object, String>() {

					@Override
					public String apply(ParameterInfo a, Object b) {
						return ParameterHandler.parameterHandler.get(a)
								.generate(a, b);
					}
				});
	}
}
