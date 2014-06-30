package laf.controllerInfo;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import laf.base.attachedProperties.AttachedPropertyBearerBase;

import com.google.common.base.Strings;

public class ControllerInfoImpl extends AttachedPropertyBearerBase implements
ControllerInfo {
	final private Class<?> controllerClass;

	final private Object type;

	private final HashMap<String, ActionMethodInfo> actionMethodsByName = new LinkedHashMap<>();
	private final HashMap<Method, ActionMethodInfo> actionMethodsByMethod = new LinkedHashMap<>();

	final boolean embedded;

	public ControllerInfoImpl(Class<?> controllerClass, Object type,
			boolean embedded) {
		this.controllerClass = controllerClass;
		this.type = type;
		this.embedded = embedded;
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
		if (!actionMethodsByName.containsKey(name)) {
			return name;
		}

		// try other names, 'till a free one is found
		int i = 1;
		String result;
		do {
			result = name + "$" + i++;
		} while (actionMethodsByName.containsKey(result));

		return result;
	}

	@Override
	public ActionMethodInfo getActionMethodInfo(String name) {
		return actionMethodsByName.get(name);
	}

	@Override
	public Collection<ActionMethodInfo> getActionMethodInfos() {
		return actionMethodsByName.values();
	}

	public void putActionMethodInfo(ActionMethodInfo actionMethodInfo) {
		actionMethodsByName.put(actionMethodInfo.getName(), actionMethodInfo);
		actionMethodsByMethod.put(actionMethodInfo.getMethod(),
				actionMethodInfo);
	}

	@Override
	public ActionMethodInfo getActionMethodInfo(Method method) {
		return actionMethodsByMethod.get(method);
	}

	@Override
	public Object getType() {
		return type;
	}

	@Override
	public boolean isEmbeddedController() {
		return embedded;
	}

}
