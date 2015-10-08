package com.github.ruediste.rise.core;

import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.util.Optional;
import java.util.function.Function;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import com.github.ruediste.rise.core.persistence.PersistenceRestartableModule;
import com.github.ruediste.rise.core.scopes.HttpScopeModule;
import com.github.ruediste.rise.core.security.authorization.MethodAuthorizationManager;
import com.github.ruediste.rise.core.web.assetDir.AssetDir;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.nonReloadable.NonRestartable;
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
import com.github.ruediste.salta.standard.DependencyKey;
import com.github.ruediste.salta.standard.InjectionPoint;
import com.github.ruediste.salta.standard.ScopeRule;
import com.github.ruediste.salta.standard.config.StandardInjectorConfiguration;
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
        new MethodAuthorizationManager().register(binder());
    }

    private void registerMessagesRule() {
        bindCreationRule(new CreationRule() {

            @Override
            public Optional<Function<RecipeCreationContext, SupplierRecipe>> apply(
                    CoreDependencyKey<?> key, CoreInjector injector) {
                if (key.getRawType().isAnnotationPresent(TMessages.class)) {
                    DependencyKey<TMessageUtil> utilKey = DependencyKey
                            .of(TMessageUtil.class);
                    String typeName = Type.getDescriptor(key.getRawType());

                    return Optional.of(
                            new Function<RecipeCreationContext, SupplierRecipe>() {
                        @Override
                        public SupplierRecipe apply(RecipeCreationContext ctx) {
                            SupplierRecipe utilRecipe = ctx.getRecipe(utilKey);
                            return new SupplierRecipe() {

                                @Override
                                protected Class<?> compileImpl(
                                        GeneratorAdapter mv,
                                        MethodCompilationContext ctx) {
                                    utilRecipe.compile(ctx);
                                    mv.visitLdcInsn(Type.getType(typeName));
                                    mv.visitMethodInsn(INVOKEVIRTUAL,
                                            "com/github/ruediste1/i18n/message/TMessageUtil",
                                            "getMessageInterfaceInstance",
                                            "(Ljava/lang/Class;)Ljava/lang/Object;",
                                            false);
                                    return Object.class;
                                }
                            };
                        }
                    });
                }
                return Optional.empty();
            }
        });
    }

    private void installPersistenceDynamicModule() {
        install(new PersistenceRestartableModule(permanentInjector));
    }

    protected void registerAssetBundleScopeRule() {
        StandardInjectorConfiguration standardConfig = config().standardConfig;
        config().standardConfig.scope.scopeRules.add(new ScopeRule() {

            @Override
            public Scope getScope(TypeToken<?> type) {
                if (TypeToken.of(AssetBundle.class).isAssignableFrom(type)
                        || TypeToken.of(AssetDir.class)
                                .isAssignableFrom(type)) {
                    return standardConfig.singletonScope;
                }
                return null;
            }
        });
    }

    protected void registerPermanentRule() {
        config().standardConfig.creationPipeline.coreCreationRuleSuppliers
                .add(0, () -> new CreationRule() {

                    @Override
                    public Optional<Function<RecipeCreationContext, SupplierRecipe>> apply(
                            CoreDependencyKey<?> key, CoreInjector injector) {
                        boolean applies = false;
                        if (key.getRawType()
                                .isAnnotationPresent(NonRestartable.class))
                            applies = true;
                        else if (key instanceof InjectionPoint<?>) {
                            if (key.getAnnotatedElement().isAnnotationPresent(
                                    NonRestartable.class)) {
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
