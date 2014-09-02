package laf.mvc.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import laf.core.base.ActionResult;
import laf.mvc.core.api.MEmbeddedController;
import laf.mvc.core.api.MUpdating;

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
