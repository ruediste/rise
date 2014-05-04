package laf.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.google.common.base.Strings;

@Singleton
public class ConfigurationFactory {

	@Inject
	Logger log;

	private Properties properties;

	@PostConstruct
	void loadProperties() {
		properties = new Properties();
		String fileName = "configuration.properties";
		try (InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(fileName)) {
			if (in != null) {
				properties.load(in);
			}
		} catch (IOException e) {
			log.warn("Can't load properties file " + fileName
					+ " from classpath", e);
		}
	}

	String getString(InjectionPoint p) {
		// search with the qualified name first
		String configKey = p.getMember().getDeclaringClass().getName() + "."
				+ p.getMember().getName();
		if (!properties.containsValue(configKey)) {
			// try with the unqualified class name
			configKey = p.getMember().getDeclaringClass().getSimpleName() + "."
					+ p.getMember().getName();
			if (!properties.containsValue(configKey)) {
				// try with just the member name
				configKey = p.getMember().getName();

				if (!properties.containsValue(configKey)) {
					// check if the annotation specifies a default
					ConfigValue annotation = p.getAnnotated().getAnnotation(
							ConfigValue.class);
					if (annotation != null
							&& !Strings.isNullOrEmpty(annotation.value())) {
						return annotation.value();
					} else {
						throw new RuntimeException(
								"No value found for configuration parameter "
										+ p.getMember());
					}
				}
			}
		}

		return properties.getProperty(configKey);
	}

	@Produces
	@ConfigValue
	public String produceString(InjectionPoint p) {
		return getString(p);
	}

	@Produces
	@ConfigValue
	public int produceInt(InjectionPoint p) {
		return Integer.parseInt(getString(p));
	}

	@Produces
	@ConfigValue
	public double produceDouble(InjectionPoint p) {
		return Double.parseDouble(getString(p));
	}

	@Produces
	@ConfigValue
	public double produceLong(InjectionPoint p) {
		return Long.parseLong(getString(p));
	}

	@SuppressWarnings("unchecked")
	@Produces
	@ConfigValue
	public <T> Class<T> produceClass(InjectionPoint p)
			throws ClassNotFoundException {
		return (Class<T>) Thread.currentThread().getContextClassLoader()
				.loadClass(getString(p));
	}
}
