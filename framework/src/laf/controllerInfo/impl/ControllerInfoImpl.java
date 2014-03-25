package laf.controllerInfo.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import laf.attachedProperties.AttachedPropertyBearerBase;
import laf.controllerInfo.ActionMethodInfo;
import laf.controllerInfo.ControllerInfo;

import com.google.common.base.Strings;

public class ControllerInfoImpl extends AttachedPropertyBearerBase implements
		ControllerInfo {
	final private Class<?> controllerClass;

	private final HashMap<String, ActionMethodInfo> actionMethods = new LinkedHashMap<>();

	public ControllerInfoImpl(Class<?> controllerClass) {
		this.controllerClass = controllerClass;
	}

	@Override
	public String getName() {
		String name = getControllerClass().getSimpleName();
		if (name.endsWith("Controller")) {
			name = name.substring(0, name.length() - "Controller".length());
		}
		return name;
	}

	@Override
	public String getPackage() {
		return getControllerClass().getPackage().getName();
	}

	@Override
	public String getQualifiedName() {
		if (Strings.isNullOrEmpty(getPackage())) {
			return getName();
		}
		return getPackage() + "." + getName();
	}

	@Override
	public Class<?> getControllerClass() {
		return controllerClass;
	}

	public String calculateUnusedMethodName(String name) {
		// return the plain name if it is not taken already
		if (!actionMethods.containsKey(name)) {
			return name;
		}

		// try other names, 'till a free one is found
		int i = 1;
		String result;
		do {
			result = name + "$" + i++;
		} while (actionMethods.containsKey(result));

		return result;
	}

	@Override
	public ActionMethodInfo getActionMethodInfo(String name) {
		return getActionMethods().get(name);
	}

	@Override
	public Iterable<ActionMethodInfo> getActionMethodInfos() {
		return getActionMethods().values();
	}

	public HashMap<String, ActionMethodInfo> getActionMethods() {
		return actionMethods;
	}

}
