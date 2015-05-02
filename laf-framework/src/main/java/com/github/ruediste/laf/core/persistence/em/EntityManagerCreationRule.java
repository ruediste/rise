package com.github.ruediste.laf.core.persistence.em;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.EntityManager;

import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;

import org.objectweb.asm.commons.GeneratorAdapter;

import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.core.CreationRule;
import com.github.ruediste.salta.core.RecipeCreationContext;
import com.github.ruediste.salta.core.compile.MethodCompilationContext;
import com.github.ruediste.salta.core.compile.SupplierRecipe;
import com.github.ruediste.salta.standard.DependencyKey;
import com.github.ruediste.salta.standard.config.StandardInjectorConfiguration;

/**
 * Create {@link EntityManager} proxies
 */
public class EntityManagerCreationRule implements CreationRule {
	private StandardInjectorConfiguration config;

	public EntityManagerCreationRule(StandardInjectorConfiguration config) {
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
							EntityManagerHolder holder = (EntityManagerHolder) ctx
									.getCompiler()
									.compileSupplier(holderRecipe).getNoThrow();

							EntityManager proxy = (EntityManager) Enhancer
									.create(EntityManager.class,
											new Dispatcher() {

												@Override
												public Object loadObject()
														throws Exception {
													return holder
															.getEntityManager(requiredQualifier == null ? null
																	: requiredQualifier
																			.annotationType());
												}
											});

							return new SupplierRecipe() {

								@Override
								protected Class<?> compileImpl(
										GeneratorAdapter mv,
										MethodCompilationContext ctx) {
									ctx.addFieldAndLoad(EntityManager.class,
											proxy);
									return EntityManager.class;
								}
							};
						}
					});
		} else
			return Optional.empty();
	}
}
