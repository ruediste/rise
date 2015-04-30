package com.github.ruediste.laf.core.persistence.em;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.EntityManager;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.core.CreationRule;
import com.github.ruediste.salta.core.RecipeCreationContext;
import com.github.ruediste.salta.core.compile.MethodCompilationContext;
import com.github.ruediste.salta.core.compile.SupplierRecipe;
import com.github.ruediste.salta.standard.DependencyKey;
import com.github.ruediste.salta.standard.config.StandardInjectorConfiguration;

/**
 * Create
 */
public class EntityManagerCreationRule implements CreationRule {
	private StandardInjectorConfiguration config;

	EntityManagerCreationRule(StandardInjectorConfiguration config) {
		this.config = config;

	}

	@Override
	public Optional<Function<RecipeCreationContext, SupplierRecipe>> apply(
			CoreDependencyKey<?> key) {
		if (EntityManager.class.equals(key.getType().getType())) {
			return Optional
					.of(new Function<RecipeCreationContext, SupplierRecipe>() {
						@Override
						public SupplierRecipe apply(RecipeCreationContext ctx) {
							Annotation requiredQualifier = config
									.getRequiredQualifier(key);
							SupplierRecipe holderRecipe = ctx
									.getRecipe(DependencyKey
											.of(EntityManagerHolder.class));
							return new SupplierRecipe() {

								@Override
								protected Class<?> compileImpl(
										GeneratorAdapter mv,
										MethodCompilationContext ctx) {
									Class<?> result = holderRecipe.compile(ctx);
									Type holderType = Type
											.getType(EntityManagerHolder.class);

									if (!EntityManagerHolder.class
											.equals(result)) {
										mv.checkCast(holderType);
									}

									mv.invokeVirtual(
											holderType,
											Method.getMethod("javax.persistence.EntityManager getEntityManager(java.lang.Class)"));
									return EntityManager.class;
								}
							};
						}
					});
		} else
			return Optional.empty();
	}

}
