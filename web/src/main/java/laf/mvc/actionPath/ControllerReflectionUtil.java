package laf.mvc.actionPath;

import java.lang.reflect.Method;

import laf.mvc.EmbeddedController;

public class ControllerReflectionUtil {

	public static boolean isEmbeddedController(Class<?> cls) {
		return cls.isAnnotationPresent(EmbeddedController.class);
	}

	public static boolean isActionMethod(Method thisMethod) {
		// TODO Auto-generated method stub
		return false;
	}
}
