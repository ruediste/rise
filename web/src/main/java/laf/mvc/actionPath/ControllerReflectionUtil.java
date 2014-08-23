package laf.mvc.actionPath;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import laf.base.ActionResult;
import laf.mvc.api.EmbeddedController;
import laf.mvc.api.Updating;

public class ControllerReflectionUtil {

	public static boolean isEmbeddedController(Class<?> cls) {
		return cls.isAnnotationPresent(EmbeddedController.class);
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
