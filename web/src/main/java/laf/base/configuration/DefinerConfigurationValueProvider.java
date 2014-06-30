package laf.base.configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.annotation.PostConstruct;

import laf.base.Val;

import com.google.common.reflect.TypeToken;

public class DefinerConfigurationValueProvider extends
ConfigurationParameterBase {

	private final class InvocationHandlerImplementation implements
			InvocationHandler {
		public Object value;
		public boolean argSet;

		InvocationHandlerImplementation(Val<?> successorResult) {
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

	@SuppressWarnings("unchecked")
	@Override
	public <V, T extends ConfigurationParameter<V>> Val<V> provideValue(
			Class<T> configInterfaceClass, TypeToken<V> configValueClass) {
		for (Method method : definer.getClass().getMethods()) {
			// check if method matches
			if (method.getParameterTypes().length != 1) {
				continue;
			}
			Class<?> parameterType = method.getParameterTypes()[0];
			if (!configInterfaceClass.equals(parameterType)) {
				continue;
			}

			Val<V> successorResult = null;

			ExtendConfiguration extendConfiguration = method
					.getAnnotation(ExtendConfiguration.class);

			if (extendConfiguration != null && getSuccessor() != null) {
				successorResult = getSuccessor().provideValue(
						configInterfaceClass, configValueClass);
			}

			// create ConfigurationParameter proxy
			InvocationHandlerImplementation handler = new InvocationHandlerImplementation(
					successorResult);
			ConfigurationParameter<?> proxy = createProxy(configInterfaceClass,
					handler);

			// invoke method
			try {
				method.invoke(definer, proxy);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(
						"Error while invoking configuration value producer method",
						e);
			}

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
