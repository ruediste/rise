package laf.core.base;

import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

/**
 * Static factory for bean instances, for situations where dependency injection
 * is not available.
 */
@Singleton
@Startup
public class InstanceFactory {

	private static Instance<Object> instance;

	public static <T> T getInstance(Class<T> clazz, Annotation... qualifiers) {
		return instance.select(clazz, qualifiers).get();
	}

	public static <T> T getInstance(TypeLiteral<T> typeLiteral,
			Annotation... qualifiers) {
		return instance.select(typeLiteral, qualifiers).get();
	}

	@Inject
	Instance<Object> inst;

	@PostConstruct
	public void initialize() {
		instance = inst;
	}
}