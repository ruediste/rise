package com.github.ruediste.laf.core.base.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;

public class PropertiesConfigrationValueProvider extends
		ConfigurationValueProviderBase {

	Logger log;

	@Inject
	ConfigurationValueParsingService configurationValueParsingService;

	@Inject
	Injector injector;

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
	public <V, T extends ConfigurationParameter<V>> V provideValue(
			Class<T> parameterInterfaceClass, TypeToken<V> configValueType) {

		// try explicitely defined keys
		ConfigurationKey key = parameterInterfaceClass
				.getAnnotation(ConfigurationKey.class);
		if (key != null) {
			for (String k : key.value()) {
				if (properties.containsKey(k)) {
					return configurationValueParsingService.<V> parse(
							configValueType, properties.getProperty(k));
				}
			}
		}

		// try the fully qualified class name
		String k = parameterInterfaceClass.getName();
		if (properties.containsKey(k)) {
			return configurationValueParsingService.<V> parse(configValueType,
					properties.getProperty(k));
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
				return injector.getInstance(factoryClass).getValue();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		// nothing found
		return getSuccessor().provideValue(parameterInterfaceClass,
				configValueType);
	}

}
