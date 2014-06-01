package laf.configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import laf.base.Val;

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

			@Override
			public void addPropretiesFile(String path) {
				ConfigurationFactory.this.addPropretiesFile(path);
			}

			@Override
			public void add(ConfigurationValueProvider provider) {
				ConfigurationFactory.this.add(provider);

			}

			@Override
			public void add(ConfigurationDefiner definer) {
				ConfigurationFactory.this.add(definer);

			}
		};
		discoverConfigurationEvent.fire(e);
	}

	@SuppressWarnings({ "unchecked" })
	private <V, T extends ConfigurationParameter<V>> Val<V> provideValue(
			ConfigurationValueProvider provider, Class<?> configInterfaceClass,
			TypeToken<?> configValueClass) {
		return provider.provideValue((Class<T>) configInterfaceClass,
				(TypeToken<V>) configValueClass);
	}

	@Produces
	public <T extends ConfigurationParameter<?>> ConfigurationValue<T> produceConfigurationValue(
			InjectionPoint p) {
		// create arguments
		Class<?> parameterInterfaceClass = TypeToken.of(p.getType())
				.resolveType(ConfigurationValue.class.getTypeParameters()[0])
				.getRawType();
		TypeToken<?> valueType = TypeToken.of(parameterInterfaceClass)
				.resolveType(
						ConfigurationParameter.class.getTypeParameters()[0]);

		// invoke provider
		Val<?> value = provideValue(provider, parameterInterfaceClass,
				valueType);

		// throw error if no value has been found
		if (value == null) {
			throw new RuntimeException("No configuration value found for "
					+ p.getMember());
		}

		// create result
		final T parameter = createParameterProxy(parameterInterfaceClass,
				value.get());
		return new ConfigurationValueImpl<>(parameter);

	}

	@SuppressWarnings("unchecked")
	private <T extends ConfigurationParameter<?>> T createParameterProxy(
			Class<?> configInterfaceClass, final Object value) {

		return (T) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(),
				new Class<?>[] { configInterfaceClass },
				new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method,
					Object[] args) throws Throwable {
				if (getMethod.equals(method)) {
					return value;
				}
				throw new RuntimeException("Method " + method.getName()
						+ " may not be called on ConfigValue instances");
			}
		});
	}
}
