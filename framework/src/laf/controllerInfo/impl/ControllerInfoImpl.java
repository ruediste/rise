package laf.controllerInfo.impl;

import java.lang.reflect.Type;

import laf.attachedProperties.AttachedPropertyBearerBase;
import laf.controllerInfo.ControllerInfo;

import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;

public class ControllerInfoImpl extends AttachedPropertyBearerBase implements
		ControllerInfo {
	final private Type type;
	final private Class<?> clazz;

	public ControllerInfoImpl(Type type) {
		this.type = type;
		clazz = TypeToken.of(type).getRawType();
	}

	@Override
	public String getName() {
		String name = clazz.getSimpleName();
		if (name.endsWith("Controller")) {
			name = name.substring(0, name.length() - "Controller".length());
		}
		return name;
	}

	@Override
	public String getPackage() {
		return clazz.getPackage().getName();
	}

	@Override
	public String getQualifiedName() {
		if (Strings.isNullOrEmpty(getPackage())) {
			return getName();
		}
		return getPackage() + "." + getName();
	}

	@Override
	public Type getType() {
		return type;
	}

}
