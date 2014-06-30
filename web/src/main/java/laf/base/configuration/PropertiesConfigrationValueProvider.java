package laf.base.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.Val;

import org.slf4j.Logger;

import com.google.common.reflect.TypeToken;

public class PropertiesConfigrationValueProvider extends
ConfigurationParameterBase {

	@Inject
	Logger log;

	@Inject
	ConfigurationValueParsingService configurationValueParsingService;

	@Inject
	Instance<ConfigurationValueFactory<?>> factoryInstance;

	Properties properties;

	public void loadProperties(String name) {
		properties = new Properties();
		String fileName = "configuration.properties";
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();

		try (InputStream in = classLoader.getResourceAsStream(fileName)) {
			if (in != null) {
				properties.load(in);
			} else {
				log.warn("did not find properties file " + fileName
						+ " on classpath");
			}
		} catch (IOException e) {
			log.warn("Can't load properties file " + fileName
					+ " from classpath", e);
		}

	}

	@Override
	public <V, T extends ConfigurationParameter<V>> Val<V> provideValue(
			Class<T> configInterfaceClass, TypeToken<V> configValueType) {

		// try explicitely defined keys
		ConfigurationKey key = configInterfaceClass
				.getAnnotation(ConfigurationKey.class);
		if (key != null) {
			for (String k : key.value()) {
				if (properties.containsKey(k)) {
					return Val.of(configurationValueParsingService.<V> parse(
							configValueType, properties.getProperty(k)));
				}
			}
		}

		// try the fully qualified class name
		String k = configInterfaceClass.getName();
		if (properties.containsKey(k)) {
			return Val.of(configurationValueParsingService.<V> parse(
					configValueType, properties.getProperty(k)));
		}

		// try the factory
		k += ".factory";
		if (properties.containsKey(k)) {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();

			try {
				@SuppressWarnings("unchecked")
				Class<? extends ConfigurationValueFactory<V>> factoryClass = (Class<? extends ConfigurationValueFactory<V>>) classLoader
						.loadClass(properties.getProperty(k));
				return Val.of(factoryInstance.select(factoryClass).get()
						.getValue());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		// nothing found
		if (getSuccessor() == null) {
			return null;
		} else {
			return getSuccessor().provideValue(configInterfaceClass,
					configValueType);
		}
	}

}
