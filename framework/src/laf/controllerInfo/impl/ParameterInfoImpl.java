package laf.controllerInfo.impl;

import java.lang.reflect.Type;

import laf.attachedProperties.AttachedPropertyBearerBase;
import laf.controllerInfo.ActionMethodInfo;
import laf.controllerInfo.ParameterInfo;
import laf.urlMapping.ParameterHandler;

public class ParameterInfoImpl extends AttachedPropertyBearerBase implements
		ParameterInfo {

	private final Type type;
	private ActionMethodInfo method;
	private ParameterHandler parameterHandler;

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
	public ParameterHandler getParameterHandler() {
		return parameterHandler;
	}

	@Override
	public void setParameterHandler(ParameterHandler parameterHandler) {
		this.parameterHandler = parameterHandler;
	}

}
