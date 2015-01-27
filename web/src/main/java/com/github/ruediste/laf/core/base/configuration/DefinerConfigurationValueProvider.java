package com.github.ruediste.laf.core.base.configuration;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.laf.core.base.ReflectionUtil;
import com.github.ruediste.laf.core.base.Val;
import com.google.common.reflect.TypeToken;

public class DefinerConfigurationValueProvider extends
		ConfigurationValueProviderBase {

	@Inject
	Logger log;

	@Inject
	ConfigurationFactory configurationFactory;

	private final class ToBeDefinedParameterHandler implements
			InvocationHandler {
		public Object value;
		public boolean argSet;

		ToBeDefinedParameterHandler(Val<?> successorResult) {
			if (successorResult != null) {
				argSet = true;
				value = successorResult.get();
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (setMethod.equals(method)) {
				value = args[0];
				argSet = true;
				return null;
			}
			if (getMethod.equals(method)) {
				if (!argSet) {
					throw new RuntimeException(
							"The value of this configuration parameter has not been set yet");
				}
				return value;
			}
			throw new RuntimeException(
					"Method "
							+ method.getName()
							+ " may not be called on ConfigurationParameters during their value definition");
		}
	}

	ConfigurationDefiner definer;
	private Method getMethod;
	private Method setMethod;

	void setDefiner(ConfigurationDefiner definer) {
		this.definer = definer;
	}

	@PostConstruct
	void initialize() {
		try {
			setMethod = ConfigurationParameter.class.getMethod("set",
					Object.class);
			getMethod = ConfigurationParameter.class.getMethod("get");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
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
	public <V, T extends ConfigurationParameter<V>> Val<V> provideValue(
			Class<T> configInterfaceClass, TypeToken<V> configValueClass) {
		for (Method method : definer.getClass().getMethods()) {
			// check if method matches
			if (method.getParameterTypes().length < 1) {
				continue;
			}
			Class<?> parameterType = method.getParameterTypes()[0];
			if (!configInterfaceClass.equals(parameterType)) {
				continue;
			}

			Val<V> successorResult = null;

			if (ReflectionUtil.isAnnotationPresent(definer.getClass(), method,
					ExtendConfiguration.class)) {
				if (getSuccessor() != null) {
					successorResult = getSuccessor().provideValue(
							configInterfaceClass, configValueClass);
				}
				if (successorResult == null) {
					throw new ExtendedParameterNotDefined(configInterfaceClass,
							definer.getClass());
				}
			}

			ArrayList<Object> arguments = new ArrayList<Object>();

			// create ConfigurationParameter proxy
			ToBeDefinedParameterHandler handler = new ToBeDefinedParameterHandler(
					successorResult);
			arguments.add(createProxy(configInterfaceClass, handler));

			// create arguments
			arguments.addAll(Arrays
					.stream(method.getParameterTypes())
					.skip(1)
					.map(cls -> configurationFactory
							.createParameterInstance(cls))
					.collect(Collectors.toList()));

			// invoke method
			log.debug("invoking " + method);
			try {
				method.invoke(definer, arguments.toArray());
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(
						"Error while invoking configuration value producer method",
						e);
			}
			log.debug("returned from " + method);

			// check if the definer method did set the value
			if (!handler.argSet) {
				throw new RuntimeException("The config value producer method "
						+ method + " has to set the configuration value");
			}

			// return result
			return Val.of((V) handler.value);
		}

		if (getSuccessor() == null) {
			return null;
		} else {
			return getSuccessor().provideValue(configInterfaceClass,
					configValueClass);
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends ConfigurationParameter<?>> T createProxy(
			Class<?> configInterfaceClass, InvocationHandler handler) {

		return (T) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(),
				new Class<?>[] { configInterfaceClass }, handler);
	}
}
