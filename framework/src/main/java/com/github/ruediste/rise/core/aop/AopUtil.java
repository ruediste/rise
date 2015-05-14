package com.github.ruediste.rise.core.aop;

import net.sf.cglib.proxy.Enhancer;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.core.EnhancerFactory;
import com.github.ruediste.salta.core.RecipeCreationContext;
import com.github.ruediste.salta.core.RecipeEnhancer;
import com.github.ruediste.salta.jsr330.binder.Binder;

/**
 * Helper class to register {@link Interceptor}s
 */
public class AopUtil {

	private static class AopEnhancerFactory implements EnhancerFactory {

		@Override
		public RecipeEnhancer getEnhancer(RecipeCreationContext ctx,
				CoreDependencyKey<?> requestedKey) {
			Enhancer e=new Enhancer();
			e.
			// TODO Auto-generated method stub
			return null;
		}

		public void add(Interceptor interceptor) {
			// TODO Auto-generated method stub

		}

	}

	private static AttachedProperty<AttachedPropertyBearer, AopEnhancerFactory> enhancerFactoryProp = new AttachedProperty<>(
			"AopEnhancer");

	public static void register(Binder binder, Interceptor interceptor) {
		AopEnhancerFactory enhancerFactory = enhancerFactoryProp
				.setIfAbsent(
						binder.config(),
						() -> {
							AopEnhancerFactory result = new AopEnhancerFactory();
							binder.config().standardConfig.construction.instantiatorRules
									.add(0, null);
							return result;
						});
		enhancerFactory.add(interceptor);
	}
}
