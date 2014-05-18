package laf.controllerInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import laf.attachedProperties.AttachedPropertyBearerBase;
import laf.base.ActionResult;

import com.google.common.base.Joiner;

public class ActionMethodInfoImpl extends AttachedPropertyBearerBase implements
		ActionMethodInfo {

	private String name;
	private final Method method;
	private ControllerInfo controllerInfo;
	private ArrayList<ParameterInfo> parameters = new ArrayList<>();

	public ActionMethodInfoImpl(Method method) {
		this.method = method;
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
		return method.getReturnType() + " "
				+ getControllerInfo().getQualifiedName() + "." + getName()
				+ "(" + parameterString + ")";
	}

	@Override
	public boolean returnsEmbeddedController() {
		return method.getReturnType() != ActionResult.class;
	}

	@Override
	public String getSignature() {
		ArrayList<String> types = new ArrayList<>();
		for (ParameterInfo p : parameters) {
			types.add(Objects.toString(p.getType().toString()));
		}
		return method.getReturnType().getSimpleName() + " " + getName() + "("
		+ Joiner.on(",").join(types) + ")";
	}
}