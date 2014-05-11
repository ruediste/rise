package laf.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;

@ApplicationScoped
public class ConfigurationFactory {

	@Inject
	Logger log;

	private Properties properties;

	// private ServletContext servletContext;

	void initialized(@Observes ServletContextEvent e) {
		// servletContext = e.getServletContext();
	}

	@PostConstruct
	void loadProperties() {
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

	String getString(InjectionPoint p) {
		// search with the qualified name first
		String configKey = p.getMember().getDeclaringClass().getName() + "."
				+ p.getMember().getName();
		if (!properties.containsKey(configKey)) {
			// try with the unqualified class name
			configKey = p.getMember().getDeclaringClass().getSimpleName() + "."
					+ p.getMember().getName();
			if (!properties.containsKey(configKey)) {
				// try with just the member name
				configKey = p.getMember().getName();

				if (!properties.containsKey(configKey)) {
					// check if the annotation specifies a default
					ConfigValue annotation = p.getAnnotated().getAnnotation(
							ConfigValue.class);
					return annotation.value();
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

	@Inject
	Instance<Object> instance;

	@SuppressWarnings("unchecked")
	@Produces
	@ConfigValue
	public <T> ConfigInstance<T> produceInstance(InjectionPoint p)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		final Object inst = createObject(getString(p));
		return new ConfigInstance<T>() {

			@Override
			public T get() {
				return (T) inst;
			}
		};
	}

	@Produces
	@ConfigValue
	public <T> List<T> produceInstancesList(InjectionPoint p)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		return produceInstancesArrayList(p);
	}

	@Produces
	@ConfigValue
	public <T> Collection<T> produceInstancesCollection(InjectionPoint p)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		return produceInstancesArrayList(p);
	}

	@Produces
	@ConfigValue
	public <T> Deque<T> produceInstancesDequeue(InjectionPoint p)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		return produceInstancesLinkedList(p);
	}

	@Produces
	@ConfigValue
	public <T> ArrayList<T> produceInstancesArrayList(InjectionPoint p)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {

		final ArrayList<T> objects = new ArrayList<>();
		addInstances(p, objects);
		return objects;
	}

	@Produces
	@ConfigValue
	public <T> LinkedList<T> produceInstancesLinkedList(InjectionPoint p)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {

		final LinkedList<T> objects = new LinkedList<>();
		addInstances(p, objects);
		return objects;
	}

	@SuppressWarnings("unchecked")
	private <T> void addInstances(InjectionPoint p, final List<T> objects)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		for (String value : getString(p).split(",")) {
			objects.add((T) createObject(value));
		}
	}

	private Object createObject(String value) throws ClassNotFoundException,
	InstantiationException, IllegalAccessException {
		String[] parts = value.split(":");
		final List<Annotation> qualifiers = new ArrayList<>();
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		for (int i = 0; i < parts.length - 1; i++) {
			Class<?> annotationClass = classLoader.loadClass(parts[i]);
			Object annotation = annotationClass.newInstance();
			qualifiers.add((Annotation) annotation);
		}
		final Class<?> objectClass = classLoader
				.loadClass(trim(parts[parts.length - 1]));
		return instance.select(objectClass,
				qualifiers.toArray(new Annotation[] {})).get();
	}

	private String trim(String string) {
		String result = string;
		while (result.length() > 0 && result.charAt(0) == ' ') {
			result = result.substring(1);
		}
		while (result.length() > 0 && result.charAt(result.length() - 1) == ' ') {
			result = result.substring(0, result.length() - 2);
		}
		return result;
	}
}
