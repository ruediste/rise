package com.github.ruediste.laf.core.front.reload;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.core.CreationRule;
import com.github.ruediste.salta.core.RecipeCreationContext;
import com.github.ruediste.salta.core.compile.SupplierRecipe;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.binder.Binder;
import com.github.ruediste.salta.standard.config.StandardInjectorConfiguration.CreationPipelineConfiguration;

public class SpaceCheckUtil {
	public static void addClassSpaceCheck(Class<?> expectedClassSpace,
			Binder binder, Injector permanentInjector) {
		CreationPipelineConfiguration creationPipeline = binder.config().standardConfig.creationPipeline;
		Supplier<CreationRule> jitRulesSupplier = creationPipeline.suppliers.jitRulesSupplier;
		int idx = creationPipeline.coreCreationRuleSuppliers
				.indexOf(jitRulesSupplier);
		creationPipeline.coreCreationRuleSuppliers.set(
				idx,
				() -> {
					CreationRule jitRule = jitRulesSupplier.get();
					ClassSpaceCache cache = permanentInjector
							.getInstance(ClassSpaceCache.class);
					return new CreationRule() {

						@Override
						public Optional<Function<RecipeCreationContext, SupplierRecipe>> apply(
								CoreDependencyKey<?> key) {
							Optional<Function<RecipeCreationContext, SupplierRecipe>> result = jitRule
									.apply(key);
							if (result.isPresent()) {
								Class<?> rawType = key.getRawType();
								Class<?> classSpace = cache
										.getClassSpace(rawType.getName());
								if (classSpace != expectedClassSpace)
									throw new RuntimeException(
											"Attempt to create an instance of "
													+ rawType.getName()
													+ " which is in the "
													+ classSpace
															.getSimpleName()
													+ " using a JIT binding from an injector for the "
													+ expectedClassSpace
															.getSimpleName());
							}
							return result;
						}
					};
				});
	}
}
