package laf.httpRequestMapping.twoStageMappingRule;

import laf.actionPath.ActionPath;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

public interface ParameterMapper {

	ActionPath<ParameterValueProvider> parse(ActionPath<String> path);

	ActionPath<String> generate(ActionPath<Object> path);

}
