package laf.http.requestMapping.twoStageMappingRule;

import laf.actionPath.ActionPath;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public interface ParameterMapper {

	ActionPath<ParameterValueProvider> parse(ActionPath<String> path);

	ActionPath<String> generate(ActionPath<Object> path);

	boolean handles(ActionPath<?> path);

}
