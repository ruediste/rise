package com.github.ruediste.laf.core.base;

import java.util.Set;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.salta.jsr330.JSR330InjectorConfiguration;

public class InitializerUtil {

	private static AttachedProperty<JSR330InjectorConfiguration, Set<Class<? extends InitializerBase>>> initializers = new AttachedProperty<>(
			"initializers");

	public static void registerInitializer(JSR330InjectorConfiguration config,
			Class<? extends InitializerBase> initializer) {

	}
}
