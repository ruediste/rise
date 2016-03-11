package com.github.ruediste.rise.core;

import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.util.Optional;
import java.util.function.Function;

import javax.inject.Singleton;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import com.github.ruediste.rise.core.i18n.RiseResourceBundleResolver;
import com.github.ruediste.rise.core.i18n.RiseValidationMessageInterpolator;
import com.github.ruediste.rise.core.persistence.PersistenceRestartableModule;
import com.github.ruediste.rise.core.scopes.HttpScopeModule;
import com.github.ruediste.rise.core.security.authorization.MethodAuthorizationManager;
import com.github.ruediste.rise.core.web.assetDir.AssetDir;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.nonReloadable.NonRestartable;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.core.CoreInjector;
import com.github.ruediste.salta.core.CreationRule;
import com.github.ruediste.salta.core.RecipeCreationContext;
import com.github.ruediste.salta.core.Scope;
import com.github.ruediste.salta.core.compile.MethodCompilationContext;
import com.github.ruediste.salta.core.compile.SupplierRecipe;
import com.github.ruediste.salta.core.compile.SupplierRecipeImpl;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Provides;
import com.github.ruediste.salta.standard.DependencyKey;
import com.github.ruediste.salta.standard.InjectionPoint;
import com.github.ruediste.salta.standard.ScopeRule;
import com.github.ruediste.salta.standard.config.StandardInjectorConfiguration;
import com.github.ruediste1.i18n.lString.AdditionalResourceKeyProvider;
import com.github.ruediste1.i18n.lString.DefaultPatternStringResolver;
import com.github.ruediste1.i18n.lString.PatternStringResolver;
import com.github.ruediste1.i18n.lString.ResouceBundleTranslatedStringResolver;
import com.github.ruediste1.i18n.lString.ResourceBundleResolver;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;
import com.github.ruediste1.i18n.message.TMessageUtil;
import com.github.ruediste1.i18n.message.TMessages;
import com.google.common.reflect.TypeToken;

public class CoreRestartableModule extends AbstractModule {

	private Injector permanentInjector;

	public CoreRestartableModule(Injector permanentInjector) {
		this.permanentInjector = permanentInjector;

	}

	@Override
	protected void configure() throws Exception {
		InitializerUtil.register(config(), CoreRestartableInitializer.class);
		InitializerUtil.register(config(), RemotUnitTestInitializer.class);
		installHttpScopeModule();
		installPersistenceDynamicModule();
		registerPermanentRule();
		registerAssetBundleScopeRule();
		registerMessagesRule();
		MethodAuthorizationManager.get(binder());
		bindPatternStringResolver();
	}

	protected void bindPatternStringResolver() {
		bind(PatternStringResolver.class).to(DefaultPatternStringResolver.class).in(Singleton.class);
	}

	@Provides
	@Singleton
	protected ResourceBundleResolver resourceBundleResolver(RiseResourceBundleResolver resolver) {
		return resolver;
	}

	@Provides
	@Singleton
	protected TranslatedStringResolver translatedStringResolver(ResouceBundleTranslatedStringResolver resolver,
			ClassHierarchyIndex idx) {
		resolver.registerAdditionalResourceKeys(
				idx.getAllChildClasses(AdditionalResourceKeyProvider.class, getClass().getClassLoader()));
		return resolver;
	}

	@Provides
	@Singleton
	protected MessageInterpolator messageInterpolator(RiseValidationMessageInterpolator interpolator) {
		return interpolator;
		// return new ResourceBundleMessageInterpolator(
		// resolver::getResourceBundle);
	}

	@Provides
	@Singleton
	protected ValidatorFactory validatorFactory(MessageInterpolator messageInterpolator) {
		return Validation.byProvider(HibernateValidator.class).configure().messageInterpolator(messageInterpolator)
				.buildValidatorFactory();
	}

	@Provides
	@Singleton
	protected Validator validator(ValidatorFactory factory) {
		return factory.getValidator();
	}

	@Provides
	protected ClassLoader restartableClassLoader() {
		return getClass().getClassLoader();
	}

	protected void registerMessagesRule() {
		bindCreationRule(createMessagesRule());
	}

	public static CreationRule createMessagesRule() {
		return new CreationRule() {

			@Override
			public Optional<Function<RecipeCreationContext, SupplierRecipe>> apply(CoreDependencyKey<?> key,
					CoreInjector injector) {
				if (key.getRawType().isAnnotationPresent(TMessages.class)) {
					DependencyKey<TMessageUtil> utilKey = DependencyKey.of(TMessageUtil.class);
					String typeName = Type.getDescriptor(key.getRawType());

					return Optional.of(new Function<RecipeCreationContext, SupplierRecipe>() {
						@Override
						public SupplierRecipe apply(RecipeCreationContext ctx) {
							SupplierRecipe utilRecipe = ctx.getRecipe(utilKey);
							return new SupplierRecipe() {

								@Override
								protected Class<?> compileImpl(GeneratorAdapter mv, MethodCompilationContext ctx) {
									utilRecipe.compile(ctx);
									ctx.addFieldAndLoad(Class.class, key.getRawType());
									// mv.visitLdcInsn(Type.getType(typeName));
									mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/ruediste1/i18n/message/TMessageUtil",
											"getMessageInterfaceInstance", "(Ljava/lang/Class;)Ljava/lang/Object;",
											false);
									return Object.class;
								}
							};
						}
					});
				}
				return Optional.empty();
			}
		};
	}

	protected void installPersistenceDynamicModule() {
		install(new PersistenceRestartableModule(permanentInjector));
	}

	protected void registerAssetBundleScopeRule() {
		StandardInjectorConfiguration standardConfig = config().standardConfig;
		config().standardConfig.scope.scopeRules.add(new ScopeRule() {

			@Override
			public Scope getScope(TypeToken<?> type) {
				if (TypeToken.of(AssetBundle.class).isAssignableFrom(type)
						|| TypeToken.of(AssetDir.class).isAssignableFrom(type)) {
					return standardConfig.singletonScope;
				}
				return null;
			}
		});
	}

	protected void registerPermanentRule() {
		config().standardConfig.creationPipeline.coreCreationRuleSuppliers.add(0, () -> new CreationRule() {

			@Override
			public Optional<Function<RecipeCreationContext, SupplierRecipe>> apply(CoreDependencyKey<?> key,
					CoreInjector injector) {
				boolean applies = false;
				if (key.getRawType().isAnnotationPresent(NonRestartable.class))
					applies = true;
				else if (key instanceof InjectionPoint<?>) {
					if (key.getAnnotatedElement().isAnnotationPresent(NonRestartable.class)) {
						applies = true;
					}
				}
				if (applies)
					return Optional.of(ctx -> new SupplierRecipeImpl(() -> permanentInjector.getInstance(key)));
				else
					return Optional.empty();
			}
		});
	}

	protected void installHttpScopeModule() {
		install(new HttpScopeModule());
	}

}
