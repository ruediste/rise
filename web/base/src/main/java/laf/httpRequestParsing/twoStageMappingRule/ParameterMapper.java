package laf.httpRequestParsing.twoStageMappingRule;

import laf.actionPath.ActionPath;
import laf.httpRequestParsing.parameterValueProvider.ParameterValueProvider;

public interface ParameterMapper {

	ActionPath<ParameterValueProvider> parse(ActionPath<String> path);

	ActionPath<String> generate(ActionPath<Object> path);

	boolean handles(ActionPath<?> path);

}
