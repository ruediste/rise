package laf.core.base.configuration;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import laf.core.base.Val;

import org.slf4j.Logger;

import com.google.common.reflect.TypeToken;

@ApplicationScoped
public class ConfigurationFactory {

	@Inject
	Logger log;

	@Inject
	Instance<DefinerConfigurationValueProvider> definerConfigurationValueProviderInstance;

	@Inject
	Instance<PropertiesConfigrationValueProvider> propertiesConfigrationValueProviderInstance;

	@Inject
	Event<DiscoverConfigruationEvent> discoverConfigurationEvent;

	private ConfigurationValueProvider provider;

	private Method getMethod;

	private Map<Class<?>, Val<?>> cache = new ConcurrentHashMap<>();

	protected void add(ConfigurationDefiner definer) {
		DefinerConfigurationValueProvider provider = definerConfigurationValueProviderInstance
				.get();
		provider.setDefiner(definer);
		add(provider);
	}

	protected void addPropretiesFile(String path) {
		PropertiesConfigrationValueProvider provider = propertiesConfigrationValueProviderInstance
				.get();
		provider.loadProperties(path);
		add(provider);
	}

	protected void add(ConfigurationValueProvider provider) {
		provider.setSuccessor(this.provider);
		this.provider = provider;

	}

	@PostConstruct
	void initialize() {

		try {
			getMethod = ConfigurationParameter.class.getMethod("get");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}

		DiscoverConfigruationEvent e = new DiscoverConfigruationEvent() {
			boolean locked;

			private void checkLocked() {
				if (locked) {
					throw new RuntimeException(
							"Event is locked. There are probably multiple observers for this event");
				}
			}

			@Override
			public void addPropretiesFile(String path) {
				checkLocked();
				ConfigurationFactory.this.addPropretiesFile(path);
			}

			@Override
			public void add(ConfigurationValueProvider provider) {
				checkLocked();
				ConfigurationFactory.this.add(provider);

			}

			@Override
			public void add(ConfigurationDefiner definer) {
				checkLocked();
				ConfigurationFactory.this.add(definer);

			}

			@Override
			public void lock() {
				locked = true;
			}
		};
		discoverConfigurationEvent.fire(e);
	}

	@SuppressWarnings({ "unchecked" })
	private <V, T extends ConfigurationParameter<V>> Val<V> provideValue(
			ConfigurationValueProvider provider, Class<?> configInterfaceClass,
			TypeToken<?> configValueClass) {

		Val<V> result;
		if (cache.containsKey(configValueClass)) {
			result = (Val<V>) cache.get(configInterfaceClass);
		} else {
			synchronized (this) {
				if (cache.containsKey(configValueClass)) {
					result = (Val<V>) cache.get(configInterfaceClass);
				} else {
					result = provider.provideValue(
							(Class<T>) configInterfaceClass,
							(TypeToken<V>) configValueClass);
					cache.put(configInterfaceClass, result);
				}
			}
		}
		return result;
	}

	@Produces
	public <T extends ConfigurationParameter<?>> ConfigurationValue<T> produceConfigurationValue(
			InjectionPoint injectionPoint) {
		// create arguments
		TypeToken<?> parameterInterfaceType = TypeToken.of(
				injectionPoint.getType()).resolveType(
				ConfigurationValue.class.getTypeParameters()[0]);
		Class<?> parameterInterfaceClass = parameterInterfaceType.getRawType();

		// create result
		final T parameter = createParameterProxy(parameterInterfaceClass,
				injectionPoint);
		return new ConfigurationValueImpl<>(parameter);

	}

	@SuppressWarnings("unchecked")
	private <T extends ConfigurationParameter<?>> T createParameterProxy(
			final Class<?> parameterInterfaceClass, final InjectionPoint injectionPoint) {

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
							Val<?> value = provideValue(provider,
									parameterInterfaceClass, valueType);

							// throw error if no value has been found
							if (value == null) {
								throw new RuntimeException(
										"No configuration value found for "
												+ parameterInterfaceClass
												+ ".\nRequired for member"
												+ injectionPoint.getMember()
														.getDeclaringClass()
												+ "." + injectionPoint.getMember().getName());
							}
						}
						throw new RuntimeException("Method " + method.getName()
								+ " may not be called on ConfigValue instances");
					}
				});
	}
}
