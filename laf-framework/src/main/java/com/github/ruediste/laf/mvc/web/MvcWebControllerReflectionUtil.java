package com.github.ruediste.laf.mvc.web;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.mvc.Updating;

public class MvcWebControllerReflectionUtil {

	public static boolean isEmbeddedController(Class<?> cls) {
		return IEmbeddedControllerMvcWeb.class.isAssignableFrom(cls);
	}

	public static boolean isActionMethod(Method method) {
		return Modifier.isPublic(method.getModifiers())
				&& (ActionResult.class.isAssignableFrom(method.getReturnType()) || isEmbeddedController(method
						.getReturnType()));
	}

	public static boolean isUpdating(Method method) {
		return method.isAnnotationPresent(Updating.class);
	}
}
