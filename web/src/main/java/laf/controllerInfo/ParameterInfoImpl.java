package laf.controllerInfo;

import java.lang.reflect.Type;

import laf.attachedProperties.AttachedPropertyBearerBase;

public class ParameterInfoImpl extends AttachedPropertyBearerBase implements
ParameterInfo {

	private final Type type;
	private ActionMethodInfo method;

	public ParameterInfoImpl(Type type) {
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public ActionMethodInfo getMethod() {
		return method;
	}

	public void setMethod(ActionMethodInfo method) {
		this.method = method;
	}

	@Override
	public String toString() {
		return "parameter of type " + type + " in method " + method;
	}
}
