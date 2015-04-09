package com.github.ruediste.laf.core.base;

import java.util.*;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.salta.jsr330.*;
import com.github.ruediste.salta.standard.config.StandardInjectorConfiguration;
import com.google.common.collect.Lists;

/**
 * Utility to register {@link Initializer}s in Salta modules and execute them
 * afterwards.
 *
 * <p>
 * The initializers can be registered in the {@link AbstractModule#configure()
 * configure()} method of Salta modules:
 *
 * <pre>
 * protected void configure() throws Exception {
 *   ...
 *   InitializerUtil.register(config(), MyInitializer.class);
 *   ...
 * }
 * </pre>
 *
 * After constructing the {@link Injector}, the initializers can be executed
 * using {@link #runInitializers(Injector)}. They are executed in the reverse
 * order they are registered. If a single class is registered multiple times,
 * the last registration determines when the corresponding initializer is
 * executed.
 * </p>
 */
public class InitializerUtil {

	private static AttachedProperty<AttachedPropertyBearer, Set<Class<? extends Initializer>>> initializers = new AttachedProperty<>(
			"initializers");

	/**
	 * Register an initializer for later execution
	 */
	public static void register(JSR330InjectorConfiguration config,
			Class<? extends Initializer> initializer) {
		Set<Class<? extends Initializer>> set = initializers.setIfAbsent(
				config, () -> new LinkedHashSet<>());
		set.remove(initializer);
		set.add(initializer);
	}

	/**
	 * Run the registered initializers
	 */
	public static void runInitializers(Injector injector) {
		StandardInjectorConfiguration config = injector.getDelegate()
				.getConfig();
		Set<Class<? extends Initializer>> set = initializers.get(config);
		if (set == null) {
			return;
		}
		for (Class<? extends Initializer> cls : Lists.reverse(new ArrayList<>(
				set))) {
			injector.getInstance(cls).initialize();
		}
	}
}
