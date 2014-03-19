package laf.controllerInfo.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;

import laf.attachedProperties.AttachedPropertyBearerBase;
import laf.controllerInfo.*;

public class ActionMethodInfoImpl extends AttachedPropertyBearerBase implements
		ActionMethodInfo {

	private final Method method;
	private ControllerInfo controllerInfo;
	private ArrayList<ParameterInfo> parameters = new ArrayList<>();

	public ActionMethodInfoImpl(Method method) {
		this.method = method;
	}

	public void addParameter(ParameterInfo parameter) {
		parameters.add(parameter);
	}

	@Override
	public Iterable<ParameterInfo> getParameters() {
		return parameters;
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public ControllerInfo getControllerInfo() {
		return controllerInfo;
	}

	public void setControllerInfo(ControllerInfo controller) {
		controllerInfo = controller;
	}

}
