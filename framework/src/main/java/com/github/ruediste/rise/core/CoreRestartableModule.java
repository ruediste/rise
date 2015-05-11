package com.github.ruediste.rise.core;

import java.util.Optional;
import java.util.function.Function;

import com.github.ruediste.rise.core.persistence.PersistenceDynamicModule;
import com.github.ruediste.rise.core.scopes.HttpScopeModule;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.nonReloadable.Permanent;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.core.CoreInjector;
import com.github.ruediste.salta.core.CreationRule;
import com.github.ruediste.salta.core.RecipeCreationContext;
import com.github.ruediste.salta.core.Scope;
import com.github.ruediste.salta.core.compile.SupplierRecipe;
import com.github.ruediste.salta.core.compile.SupplierRecipeImpl;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.standard.InjectionPoint;
import com.github.ruediste.salta.standard.ScopeRule;
import com.github.ruediste.salta.standard.config.StandardInjectorConfiguration;
import com.google.common.reflect.TypeToken;

public class CoreRestartableModule extends AbstractModule {

	private Injector permanentInjector;

	public CoreRestartableModule(Injector permanentInjector) {
		this.permanentInjector = permanentInjector;

	}

	@Override
	protected void configure() throws Exception {
		InitializerUtil.register(config(), CoreDynamicInitializer.class);
		installHttpScopeModule();
		installPersistenceDynamicModule();
		registerPermanentRule();
		registerAssetBundleScopeRule();
	}

	private void installPersistenceDynamicModule() {
		install(new PersistenceDynamicModule(permanentInjector));
	}

	protected void registerAssetBundleScopeRule() {
		StandardInjectorConfiguration standardConfig = config().standardConfig;
		config().standardConfig.scope.scopeRules.add(new ScopeRule() {

			@Override
			public Scope getScope(TypeToken<?> type) {
				if (TypeToken.of(AssetBundle.class).isAssignableFrom(type)) {
					return standardConfig.singletonScope;
				}
				return null;
			}
		});
	}

	protected void registerPermanentRule() {
		config().standardConfig.creationPipeline.coreCreationRuleSuppliers.add(
				0, () -> new CreationRule() {

					@Override
					public Optional<Function<RecipeCreationContext, SupplierRecipe>> apply(
							CoreDependencyKey<?> key, CoreInjector injector) {
						boolean applies = false;
						if (key.getRawType().isAnnotationPresent(
								Permanent.class))
							applies = true;
						else if (key instanceof InjectionPoint<?>) {
							if (key.getAnnotatedElement().isAnnotationPresent(
									Permanent.class)) {
								applies = true;
							}
						}
						if (applies)
							return Optional.of(ctx -> new SupplierRecipeImpl(
									() -> permanentInjector.getInstance(key)));
						else
							return Optional.empty();
					}
				});
	}

	protected void installHttpScopeModule() {
		install(new HttpScopeModule());
	}

}
