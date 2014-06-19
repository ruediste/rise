package laf.controllerInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import laf.attachedProperties.AttachedPropertyBearerBase;
import laf.base.ActionResult;

import com.google.common.base.Joiner;
import com.google.common.reflect.TypeToken;

public class ActionMethodInfoImpl extends AttachedPropertyBearerBase implements
		ActionMethodInfo {

	private String name;
	private Method method;
	private Type returnType;
	private ControllerInfo controllerInfo;
	private ArrayList<ParameterInfo> parameters = new ArrayList<>();
	private boolean updating;

	public ActionMethodInfoImpl() {

	}

	public ActionMethodInfoImpl(Method method) {
		this.method = method;
		returnType = method.getGenericReturnType();
	}

	@Override
	public List<ParameterInfo> getParameters() {
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

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		ArrayList<String> parameterTypes = new ArrayList<>();
		for (ParameterInfo p : parameters) {
			parameterTypes.add(p.getType().toString());
		}
		String parameterString = Joiner.on(", ").join(parameterTypes);
		return getReturnType() + " " + getControllerInfo().getQualifiedName()
				+ "." + getName() + "(" + parameterString + ")";
	}

	@Override
	public boolean returnsEmbeddedController() {
		return getReturnType() != ActionResult.class;
	}

	@Override
	public String getSignature() {
		ArrayList<String> types = new ArrayList<>();
		for (ParameterInfo p : parameters) {
			types.add(Objects.toString(p.getType().toString()));
		}
		return TypeToken.of(getReturnType()).getRawType().getSimpleName() + " "
		+ getName() + "(" + Joiner.on(",").join(types) + ")";
	}

	@Override
	public boolean isUpdating() {
		return updating;
	}

	public void setUpdating(boolean updating) {
		this.updating = updating;
	}

	public Type getReturnType() {
		return returnType;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
}
