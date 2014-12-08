package laf.component.core.translation;

import java.lang.reflect.Field;

public class TranslationKeyUtil {

	public static Object getKey(Class<?> clazz, Field field) {
		return clazz.getName() + "." + field.getName();
	}

}
