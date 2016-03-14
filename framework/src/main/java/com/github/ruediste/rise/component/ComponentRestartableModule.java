package com.github.ruediste.rise.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import org.objectweb.asm.commons.GeneratorAdapter;

import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.core.CoreInjector;
import com.github.ruediste.salta.core.CreationRule;
import com.github.ruediste.salta.core.RecipeCreationContext;
import com.github.ruediste.salta.core.compile.ConstantSupplierRecipe;
import com.github.ruediste.salta.core.compile.MethodCompilationContext;
import com.github.ruediste.salta.core.compile.SupplierRecipe;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.JSR330InjectorConfiguration;
import com.github.ruediste.salta.standard.DependencyKey;
import com.github.ruediste.salta.standard.ScopeImpl;
import com.github.ruediste.salta.standard.recipe.FixedMethodInvocationFunctionRecipe;
import com.google.common.reflect.TypeToken;

public class ComponentRestartableModule extends AbstractModule {

    @SuppressWarnings("unused")
    private Injector permanentInjector;

    public ComponentRestartableModule(Injector permanentInjector) {
        this.permanentInjector = permanentInjector;

    }

    private static Constructor<?> bindingGroupConstructor;
    private static Method bindingGroupValueTypeTokenInitialize;
    private static Method bindingGroupTypeTokenInitialize;

    static {
        try {
            bindingGroupConstructor = BindingGroup.class.getDeclaredConstructor();
            bindingGroupValueTypeTokenInitialize = BindingGroup.class.getMethod("initialize", Object.class,
                    TypeToken.class);
            bindingGroupTypeTokenInitialize = BindingGroup.class.getMethod("initialize", TypeToken.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void configure() throws Exception {
        bindBindingGroupCreationRule();

        PageScopeManager scopeHandler = new PageScopeManager();

        bindScope(PageScoped.class, new ScopeImpl(scopeHandler));
        bind(PageScopeManager.class).toInstance(scopeHandler);

        InitializerUtil.register(config(), ComponentRestartableInitializer.class);
    }

    protected void bindBindingGroupCreationRule() {
        JSR330InjectorConfiguration config = config();
        bindCreationRule(new CreationRule() {

            @Override
            public Optional<Function<RecipeCreationContext, SupplierRecipe>> apply(CoreDependencyKey<?> key,
                    CoreInjector injector) {
                if (!key.getRawType().equals(BindingGroup.class))
                    return Optional.empty();

                return Optional.of(ctx -> {
                    TypeToken<?> dataType = key.getType().resolveType(BindingGroup.class.getTypeParameters()[0]);

                    SupplierRecipe constructionRecipe = config.standardConfig.construction
                            .createConcreteConstructionRecipe(key.getType(), ctx);

                    if (TypeToken.of(Object.class).equals(dataType)) {
                        return constructionRecipe;
                    }

                    FixedMethodInvocationFunctionRecipe initializeRecipe = ctx.tryGetRecipe(DependencyKey.of(dataType))
                            .map(arg -> new FixedMethodInvocationFunctionRecipe(bindingGroupValueTypeTokenInitialize,
                                    Arrays.asList(arg,
                                            new ConstantSupplierRecipe<TypeToken<?>>(TypeToken.class, dataType))))
                            .orElseGet(() -> new FixedMethodInvocationFunctionRecipe(bindingGroupTypeTokenInitialize,
                                    Arrays.asList(
                                            new ConstantSupplierRecipe<TypeToken<?>>(TypeToken.class, dataType))));
                    return new SupplierRecipe() {

                        @Override
                        protected Class<?> compileImpl(GeneratorAdapter mv, MethodCompilationContext ctx) {
                            Class<?> t = constructionRecipe.compile(ctx);
                            mv.dup();
                            initializeRecipe.compile(t, ctx);
                            return t;
                        }
                    };

                });
            }
        });
    }
}
