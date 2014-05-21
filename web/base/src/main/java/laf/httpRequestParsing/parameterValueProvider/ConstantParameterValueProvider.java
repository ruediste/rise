package laf.httpRequestParsing.parameterValueProvider;

import java.util.Objects;

public class ConstantParameterValueProvider implements ParameterValueProvider {

	private Object value;

	public ConstantParameterValueProvider(Object value) {
		this.value = value;

	}

	@Override
	public Object get() {
		return value;
	}

	@Override
	public boolean providesNonEqualValue(Object other) {
		return !Objects.equals(value, other);
	}

}
