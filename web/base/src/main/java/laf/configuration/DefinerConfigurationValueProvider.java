package laf.configuration;

import java.lang.reflect.*;

import javax.annotation.PostConstruct;

import laf.base.Val;

import com.google.common.reflect.TypeToken;

public class DefinerConfigurationValueProvider implements
		ConfigurationValueProvider {

	private final class InvocationHandlerImplementation implements
			InvocationHandler {
		public Object value;
		public boolean argSet;

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
			if (method.getParameterTypes().length != 1) {
				continue;
			}
			Class<?> parameterType = method.getParameterTypes()[0];
			if (!configInterfaceClass.equals(parameterType)) {
				continue;
			}
			InvocationHandlerImplementation handler = new InvocationHandlerImplementation();
			ConfigurationParameter<?> proxy = createProxy(configInterfaceClass,
					handler);
			try {
				method.invoke(definer, proxy);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(
						"Error while invoking configuration value producer method",
						e);
			}
			if (!handler.argSet) {
				throw new RuntimeException("The config value producer method "
						+ method + " has to set the configuration value");
			}

			return Val.of((V) handler.value);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T extends ConfigurationParameter<?>> T createProxy(
			Class<?> configInterfaceClass, InvocationHandler handler) {

		return (T) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(),
				new Class<?>[] { configInterfaceClass }, handler);
	}
}
