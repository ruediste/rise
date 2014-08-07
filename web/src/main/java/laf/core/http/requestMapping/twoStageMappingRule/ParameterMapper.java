package laf.core.http.requestMapping.twoStageMappingRule;

import laf.core.actionPath.ActionPath;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public interface ParameterMapper {

	ActionPath<ParameterValueProvider> parse(ActionPath<String> path);

	ActionPath<String> generate(ActionPath<Object> path);

	boolean handles(ActionPath<?> path);

}
