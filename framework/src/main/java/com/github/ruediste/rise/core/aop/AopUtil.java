package com.github.ruediste.rise.core.aop;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.salta.core.CompiledSupplier;
import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.core.EnhancerFactory;
import com.github.ruediste.salta.core.RecipeCreationContext;
import com.github.ruediste.salta.core.RecipeEnhancer;
import com.github.ruediste.salta.standard.DefaultFixedConstructorInstantiationRule;
import com.github.ruediste.salta.standard.config.FixedConstructorInstantiationRule;
import com.github.ruediste.salta.standard.config.StandardInjectorConfiguration;
import com.github.ruediste.salta.standard.recipe.RecipeEnhancerImpl;
import com.github.ruediste.salta.standard.recipe.RecipeInstantiator;
import com.github.ruediste.salta.standard.recipe.RecipeInstantiatorImpl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;

/**
 * Utility to register AOP advices
 */
public class AopUtil {
	private static AttachedProperty<AttachedPropertyBearer, SubclassRule> subclassRuleProperty = new AttachedProperty<>(
			"SubclassAopRule");
	private static AttachedProperty<AttachedPropertyBearer, ProxyRule> proxyRuleProperty = new AttachedProperty<>(
			"ProxyAopRule");

	private static class AdviceEntry {

		Predicate<TypeToken<?>> typeMatcher;
		BiPredicate<TypeToken<?>, Method> methodMatcher;
		AroundAdvice advice;

		public AdviceEntry(Predicate<TypeToken<?>> typeMatcher,
				BiPredicate<TypeToken<?>, Method> methodMatcher,
				AroundAdvice advice) {
			super();
			this.typeMatcher = typeMatcher;
			this.methodMatcher = methodMatcher;
			this.advice = advice;
		}

	}

	private static class SubclassRule implements
			FixedConstructorInstantiationRule {

		private HashMap<TypeToken<?>, Optional<RecipeInstantiator>> cache = new HashMap<>();
		private ArrayList<AdviceEntry> advices = new ArrayList<>();
		private StandardInjectorConfiguration config;

		public SubclassRule(StandardInjectorConfiguration config) {
			this.config = config;
		}

		@Override
		public Optional<RecipeInstantiator> create(TypeToken<?> typeToken,
				RecipeCreationContext ctx, Constructor<?> constructor) {
			return cache.computeIfAbsent(typeToken,
					x -> createImpl(typeToken, ctx, constructor));
		}

		private Optional<RecipeInstantiator> createImpl(TypeToken<?> typeToken,
				RecipeCreationContext ctx, Constructor<?> constructor) {
			Multimap<Method, AroundAdvice> adviceMap = createAdviceMap(
					typeToken, advices);
			if (adviceMap.isEmpty())
				return Optional.empty();

			CompiledSupplier[] argSuppliers = DefaultFixedConstructorInstantiationRule
					.resolveArguments(config, typeToken, ctx, constructor)
					.stream().map(ctx.getCompiler()::compileSupplier)
					.toArray(size -> new CompiledSupplier[size]);

			return Optional
					.of(new RecipeInstantiatorImpl(
							() -> {
								AttachedPropertyBearerBase bearer = new AttachedPropertyBearerBase();
								Enhancer e = new Enhancer();
								e.setSuperclass(typeToken.getRawType());
								e.setCallback(new MethodInterceptor() {

									@Override
									public Object intercept(Object obj,
											Method method, Object[] args,
											MethodProxy proxy) throws Throwable {
										return enhance(adviceMap.get(method)
												.iterator(), obj, method, args,
												proxy);
									}

									private Object enhance(
											Iterator<AroundAdvice> iterator,
											Object obj, Method method,
											Object[] args, MethodProxy proxy)
											throws Throwable {
										if (!iterator.hasNext())
											return proxy.invokeSuper(obj, args);
										return iterator.next().intercept(
												new InterceptedInvocation() {

													@Override
													public Object proceed(
															Object... args)
															throws Throwable {
														return enhance(
																iterator, obj,
																method, args,
																proxy);
													}

													@Override
													public Object getTarget() {
														return obj;
													}

													@Override
													public Method getMethod() {
														return method;
													}

													@Override
													public Object[] getArguments() {
														return args;
													}

													@Override
													public Object proceed()
															throws Throwable {
														return enhance(
																iterator, obj,
																method, args,
																proxy);
													}

													@Override
													public AttachedPropertyBearer getPropertyBearer() {
														return bearer;
													}
												});
									}
								});

								Object[] args = new Object[argSuppliers.length];
								for (int i = 0; i < args.length; i++) {
									args[i] = argSuppliers[i].getNoThrow();
								}
								return e.create(
										constructor.getParameterTypes(), args);
							}));
		}

		public void addAdvice(AdviceEntry advice) {
			advices.add(advice);
		}

	}

	private static class ProxyRule implements EnhancerFactory {

		private HashMap<TypeToken<?>, Optional<RecipeEnhancer>> cache = new HashMap<>();
		private ArrayList<AdviceEntry> advices = new ArrayList<>();

		@Override
		public RecipeEnhancer getEnhancer(RecipeCreationContext ctx,
				CoreDependencyKey<?> requestedKey) {
			return cache.computeIfAbsent(requestedKey.getType(),
					type -> createImpl(ctx, type)).orElse(null);
		}

		private Optional<RecipeEnhancer> createImpl(RecipeCreationContext ctx,
				TypeToken<?> typeToken) {
			Multimap<Method, AroundAdvice> adviceMap = createAdviceMap(
					typeToken, advices);
			if (adviceMap.isEmpty())
				return Optional.empty();
			return Optional.of(new RecipeEnhancerImpl(delegate -> {
				Enhancer e = new Enhancer();
				e.setSuperclass(typeToken.getRawType());
				e.setCallback(new MethodInterceptor() {

					@Override
					public Object intercept(Object obj, Method method,
							Object[] args, MethodProxy proxy) throws Throwable {
						return enhance(adviceMap.get(method).iterator(), obj,
								method, args, proxy);
					}

					private Object enhance(Iterator<AroundAdvice> iterator,
							Object obj, Method method, Object[] args,
							MethodProxy proxy) throws Throwable {
						if (!iterator.hasNext())
							return proxy.invoke(delegate, args);
						return iterator.next().intercept(
								new InterceptedInvocation() {

									@Override
									public Object proceed(Object... args)
											throws Throwable {
										return enhance(iterator, obj, method,
												args, proxy);
									}

									@Override
									public Object getTarget() {
										return obj;
									}

									@Override
									public Method getMethod() {
										return method;
									}

									@Override
									public Object[] getArguments() {
										return args;
									}

									@Override
									public Object proceed() throws Throwable {
										return enhance(iterator, obj, method,
												args, proxy);
									}

									@Override
									public AttachedPropertyBearer getPropertyBearer() {
										throw new UnsupportedOperationException(
												"Not implemented for proxy enhancement");
									}
								});
					}
				});

				return e.create();
			}));
		}

		public void addAdvice(AdviceEntry advice) {
			advices.add(advice);
		}

	}

	private static Multimap<Method, AroundAdvice> createAdviceMap(
			TypeToken<?> typeToken, ArrayList<AdviceEntry> advices2) {
		Multimap<Method, AroundAdvice> adviceMap = ArrayListMultimap.create();
		{
			ArrayList<Method> methods = null;
			for (AdviceEntry entry : advices2) {
				if (entry.typeMatcher.test(typeToken)) {
					if (methods == null) {
						methods = new ArrayList<>();
						Enhancer.getMethods(typeToken.getRawType(), null,
								methods);
					}
					for (Method method : methods) {
						if (entry.methodMatcher.test(typeToken, method)) {
							adviceMap.put(method, entry.advice);
						}
					}

				}
			}
		}
		return adviceMap;
	}

	/**
	 * Register an advice using a subclass
	 * 
	 * @param config
	 * @param typeMatcher
	 * @param methodMatcher
	 * @param advice
	 */
	public static void registerSubclass(StandardInjectorConfiguration config,
			Predicate<TypeToken<?>> typeMatcher,
			BiPredicate<TypeToken<?>, Method> methodMatcher, AroundAdvice advice) {
		SubclassRule rule = subclassRuleProperty.setIfAbsent(config, () -> {
			SubclassRule tmp = new SubclassRule(config);
			config.fixedConstructorInstantiatorFactoryRules.add(0, tmp);
			return tmp;
		});
		rule.addAdvice(new AdviceEntry(typeMatcher, methodMatcher, advice));
	}

	/**
	 * register an advice using a proxy
	 * 
	 * @param config
	 * @param typeMatcher
	 * @param methodMatcher
	 * @param advice
	 */
	public static void registerProxy(StandardInjectorConfiguration config,
			Predicate<TypeToken<?>> typeMatcher,
			BiPredicate<TypeToken<?>, Method> methodMatcher, AroundAdvice advice) {
		ProxyRule rule = proxyRuleProperty.setIfAbsent(config, () -> {
			ProxyRule tmp = new ProxyRule();
			config.config.enhancerFactories.add(0, tmp);
			return tmp;
		});
		rule.addAdvice(new AdviceEntry(typeMatcher, methodMatcher, advice));
	}
}
