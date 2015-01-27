package com.github.ruediste.laf.mvc.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.mvc.core.api.MEmbeddedController;
import com.github.ruediste.laf.mvc.core.api.MUpdating;

public class ControllerReflectionUtil {

	public static boolean isEmbeddedController(Class<?> cls) {
		return cls.isAnnotationPresent(MEmbeddedController.class);
	}

	public static boolean isActionMethod(Method method) {
		return Modifier.isPublic(method.getModifiers())
				&& (ActionResult.class.isAssignableFrom(method.getReturnType()) || isEmbeddedController(method
						.getReturnType()));
	}

	public static boolean isUpdating(Method method) {
		return method.isAnnotationPresent(MUpdating.class);
	}
}
