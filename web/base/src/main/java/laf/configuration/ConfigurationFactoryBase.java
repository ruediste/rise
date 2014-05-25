package laf.configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import laf.base.Val;

import org.slf4j.Logger;

import com.google.common.reflect.TypeToken;

public abstract class ConfigurationFactoryBase {

	@Inject
	Logger log;

	@Inject
	Instance<DefinerConfigurationValueProvider> definerConfigurationValueProviderInstance;

	@Inject
	Instance<PropertiesConfigrationValueProvider> propertiesConfigrationValueProviderInstance;

	ArrayList<ConfigurationValueProvider> providers = new ArrayList<>();

	private Method getMethod;

	protected abstract void registerConfigurationValueProviders();

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
		providers.add(provider);

	}

	@PostConstruct
	void initialize() {
		registerConfigurationValueProviders();
		try {
			getMethod = ConfigurationValue.class.getMethod("get");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "unchecked" })
	private <V, T extends ConfigurationValue<V>> Val<V> provideValue(
			ConfigurationValueProvider provider, Class<?> configInterfaceClass,
			TypeToken<?> configValueClass) {
		return provider.provideValue((Class<T>) configInterfaceClass,
				(TypeToken<V>) configValueClass);
	}

	@Produces
	public <T extends ConfigurationValue<?>> T produceConfigurationValue(
			InjectionPoint p) {
		Class<?> configInterfaceClass = TypeToken.of(p.getType()).getRawType();
		TypeToken<?> valueType = TypeToken.of(configInterfaceClass)
				.resolveType(ConfigurationValue.class.getTypeParameters()[0]);
		for (ConfigurationValueProvider provider : providers) {
			Val<?> value = provideValue(provider, configInterfaceClass,
					valueType);
			if (value != null) {
				return createProxy(configInterfaceClass, value);
			}
		}

		throw new RuntimeException("No configuration value found for "
				+ p.getMember());
	}

	@SuppressWarnings("unchecked")
	private <T extends ConfigurationValue<?>> T createProxy(
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
