package com.github.ruediste.laf.core.base.configuration;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.laf.core.base.configuration.ConfigurationFactory.NoValueFoundException;
import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;

public class DefinerConfigurationValueProvider extends
		ConfigurationValueProviderBase {

	Logger log;

	@Inject
	Injector injector;

	@Inject
	ConfigurationFactory configurationFactory;

	ConfigurationDefiner definer;

	void setDefiner(ConfigurationDefiner definer) {
		this.definer = definer;
	}

	@PostConstruct
	void initialize() {
	}

	public static class ExtendedParameterNotDefined extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public ExtendedParameterNotDefined(Class<?> parameter, Class<?> definer) {
			super(
					"Definer "
							+ definer.getName()
							+ " extends configuration parameter "
							+ parameter.getName()
							+ " but the parameter is not defined by the definers "
							+ "further down the chain. Define the parameter in an underlying definer");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V, T extends ConfigurationParameter<V>> V provideValue(
			Class<T> parameterInterfaceClass, TypeToken<V> configValueClass) {
		for (Method method : definer.getClass().getMethods()) {
			if (!parameterInterfaceClass.equals(method.getReturnType())) {
				continue;
			}

			ArrayList<Object> arguments = new ArrayList<Object>();

			boolean extendConfiguration = method.getParameterCount() >= 1
					&& parameterInterfaceClass.equals(method
							.getParameterTypes()[0]);
			if (extendConfiguration) {
				try {
					Object successorResult = getSuccessor().provideValue(
							parameterInterfaceClass, configValueClass);
					arguments.add(createProxy(parameterInterfaceClass,
							new InvocationHandler() {

								@Override
								public Object invoke(Object proxy,
										Method method, Object[] args)
										throws Throwable {
									return successorResult;
								}
							}));
				} catch (NoValueFoundException e) {
					throw new ExtendedParameterNotDefined(
							parameterInterfaceClass, definer.getClass());
				}
			}

			// create arguments
			arguments.addAll(Arrays
					.stream(method.getParameterTypes())
					// skip one if the parameter instance is already present
					.skip(extendConfiguration ? 1 : 0)
					.map(cls -> injector.getInstance(cls))
					.collect(Collectors.toList()));

			// invoke method
			log.debug("invoking " + method);
			ConfigurationParameter<?> result;
			try {
				result = (ConfigurationParameter<?>) method.invoke(definer,
						arguments.toArray());
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(
						"Error while invoking configuration value producer method",
						e);
			}
			log.debug("returned from " + method);

			// return result
			return (V) result.get();
		}

		return getSuccessor().provideValue(parameterInterfaceClass,
				configValueClass);
	}

	@SuppressWarnings("unchecked")
	private <T extends ConfigurationParameter<?>> T createProxy(
			Class<?> configInterfaceClass, InvocationHandler handler) {

		return (T) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(),
				new Class<?>[] { configInterfaceClass }, handler);
	}
}
