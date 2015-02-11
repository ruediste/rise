package com.github.ruediste.laf.core.base.configuration;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.*;

import org.slf4j.Logger;

import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;

@Singleton
public class ConfigurationFactory {

	Logger log;

	@Inject
	Provider<DefinerConfigurationValueProvider> definerConfigurationValueProviderInstance;

	@Inject
	Provider<PropertiesConfigrationValueProvider> propertiesConfigrationValueProviderInstance;

	@Inject
	DefaultAnnotationConfigurationValueProvider defaultAnnotationConfigurationValueProvider;

	@Inject
	Injector injector;

	private ConfigurationValueProvider provider = new TerminalConfigurationValueProvider();

	private Method getMethod;

	private Map<Class<?>, Object> cache = new ConcurrentHashMap<>();

	public ConfigurationFactory add(
			Class<? extends ConfigurationDefiner> definer) {
		return add(injector.getInstance(definer));
	}

	public ConfigurationFactory add(ConfigurationDefiner definer) {
		DefinerConfigurationValueProvider provider = definerConfigurationValueProviderInstance
				.get();
		provider.setDefiner(definer);
		add(provider);
		return this;
	}

	public ConfigurationFactory addPropretiesFile(String path) {
		PropertiesConfigrationValueProvider provider = propertiesConfigrationValueProviderInstance
				.get();
		provider.loadProperties(path);
		add(provider);
		return this;
	}

	public ConfigurationFactory add(ConfigurationValueProvider provider) {
		provider.setSuccessor(this.provider);
		this.provider = provider;
		return this;
	}

	@PostConstruct
	void initialize() {
		add(defaultAnnotationConfigurationValueProvider);

		try {
			getMethod = ConfigurationParameter.class.getMethod("get");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings({ "unchecked" })
	private <V, T extends ConfigurationParameter<V>> V provideValue(
			Class<?> parameterInterfaceClass, TypeToken<?> configValueClass) {

		V result;
		if (cache.containsKey(parameterInterfaceClass)) {
			result = (V) cache.get(parameterInterfaceClass);
		} else {
			synchronized (this) {
				if (cache.containsKey(parameterInterfaceClass)) {
					result = (V) cache.get(parameterInterfaceClass);
				} else {
					result = provider.provideValue(
							(Class<T>) parameterInterfaceClass,
							(TypeToken<V>) configValueClass);
					if (result != null) {
						cache.put(parameterInterfaceClass, result);
					}
				}
			}
		}
		return result;
	}

	public static class NoValueFoundException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public NoValueFoundException(Class<?> parameterInterfaceClass) {
			super("No configuration value found for " + parameterInterfaceClass);
		}

	}

	public <T extends ConfigurationParameter<?>> T createParameterInstance(
			Class<?> parameterInterfaceClass, Member targetMember) {

		// create result
		try {
			return createParameterInstance(parameterInterfaceClass);
		} catch (NoValueFoundException e) {
			throw new RuntimeException(
					"Error while retrieving configuration value for "
							+ parameterInterfaceClass
							+ ".\nRequired for member"
							+ targetMember.getDeclaringClass() + "."
							+ targetMember.getName());
		}

	}

	@SuppressWarnings("unchecked")
	public <T extends ConfigurationParameter<?>> T createParameterInstance(
			final Class<?> parameterInterfaceClass) {

		return (T) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(),
				new Class<?>[] { parameterInterfaceClass },
				new InvocationHandler() {

					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						if (getMethod.equals(method)) {
							TypeToken<?> valueType = TypeToken.of(
									parameterInterfaceClass).resolveType(
									ConfigurationParameter.class
											.getTypeParameters()[0]);

							// invoke provider
							Object value = provideValue(
									parameterInterfaceClass, valueType);

							return value;
						}
						throw new RuntimeException("Method " + method.getName()
								+ " may not be called on ConfigValue instances");
					}
				});
	}
}
