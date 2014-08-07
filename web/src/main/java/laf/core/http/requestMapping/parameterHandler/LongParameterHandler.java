package laf.core.http.requestMapping.parameterHandler;

import laf.core.controllerInfo.ParameterInfo;
import laf.core.http.requestMapping.parameterValueProvider.ConstantParameterValueProvider;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public class LongParameterHandler implements ParameterHandler {

	@Override
	public boolean handles(ParameterInfo info) {
		return info.getType() == Long.class || info.getType() == Long.TYPE;
	}

	@Override
	public String generate(ParameterInfo info, Object value) {
		return String.valueOf(value);
	}

	@Override
	public ParameterValueProvider parse(ParameterInfo info, String urlPart) {
		return new ConstantParameterValueProvider(Long.parseLong(urlPart));
	}

}
