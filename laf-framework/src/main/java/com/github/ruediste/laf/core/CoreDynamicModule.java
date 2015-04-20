package com.github.ruediste.laf.core;

import java.util.Optional;
import java.util.function.Function;

import com.github.ruediste.laf.core.scopes.HttpScopeModule;
import com.github.ruediste.laf.util.InitializerUtil;
import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.core.CreationRule;
import com.github.ruediste.salta.core.RecipeCreationContext;
import com.github.ruediste.salta.core.compile.SupplierRecipe;
import com.github.ruediste.salta.core.compile.SupplierRecipeImpl;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Provides;
import com.github.ruediste.salta.standard.InjectionPoint;

public class CoreDynamicModule extends AbstractModule {

	private Injector permanentInjector;

	public CoreDynamicModule(Injector permanentInjector) {
		this.permanentInjector = permanentInjector;

	}

	@Override
	protected void configure() throws Exception {
		InitializerUtil.register(config(),
				CoreApplicationInstanceInitializer.class);
		installHttpScopeModule();
		// registerPermanentRule();
	}

	protected void registerPermanentRule() {
		config().standardConfig.creationPipeline.coreCreationRuleSuppliers.add(
				0, () -> new CreationRule() {

					@Override
					public Optional<Function<RecipeCreationContext, SupplierRecipe>> apply(
							CoreDependencyKey<?> key) {
						if (key instanceof InjectionPoint<?>) {
							if (key.getAnnotatedElement().isAnnotationPresent(
									Permanent.class)) {
								return Optional
										.of(ctx -> new SupplierRecipeImpl(
												() -> permanentInjector
														.getInstance(key)));
							}
						}
						return Optional.empty();
					}
				});
	}

	protected void installHttpScopeModule() {
		install(new HttpScopeModule());
	}

	@Provides
	@Permanent
	Injector providePermanentInjector() {
		return permanentInjector;
	}

}
