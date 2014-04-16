package laf.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.FrameworkRootInitializer;
import laf.initialization.*;

@Singleton
public class ConfigurationInitializerCreator {

	@Inject
	BeanManager beanManager;

	@Inject
	@Singleton
	Instance<Object> instance;

	@Singleton
	InitializationService initializationService;

	/**
	 * An initializer which depends on all initializers created through
	 * {@link ConfigurationParameter}s
	 */
	private class ConfigurationInitializer implements Initializer {

		Set<Class<?>> relatedRepresentingClasses = new HashSet<>();

		Set<Initializer> requiredInitializers = new HashSet<>();

		@Override
		public Collection<InitializerDependsRelation> getDeclaredRelations(
				Initializer other) {

			// make the framework root initializer depend on this initializer
			if (other.getRepresentingClass().equals(
					FrameworkRootInitializer.class)) {
				return Collections.singleton(new InitializerDependsRelation(
						other, this, false));
			}
			if (requiredInitializers.contains(other)) {
				return Collections.singleton(new InitializerDependsRelation(
						this, other, false));
			} else {
				return Collections.emptyList();
			}
		}

		@Override
		public Set<Class<?>> getRelatedRepresentingClasses() {
			return relatedRepresentingClasses;
		}

		@Override
		public Class<?> getRepresentingClass() {
			return ConfigurationInitializer.class;
		}

		@Override
		public void run() {
			// nothing to do
		}

	}

	public void initialize(@Observes CreateInitializersEvent e) {
		Singleton singleton = new Singleton() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return Singleton.class;
			}
		};

		ConfigurationInitializer configurationInitializer = new ConfigurationInitializer();
		e.addInitializer(configurationInitializer);

		for (Bean<?> bean : beanManager.getBeans(Object.class, singleton)) {
			for (Field f : bean.getBeanClass().getFields()) {
				if (ConfigurationParameter.class.isAssignableFrom(f.getType())) {
					Object o = instance.select(bean.getBeanClass()).get();
					try {
						ConfigurationParameter<?> p = (ConfigurationParameter<?>) f
								.get(o);
						// set the identifier
						p.identifier = f.getDeclaringClass().getName() + "."
								+ f.getName();

						// add eventual initializers
						{
							for (Initializer i : e.createInitializersFrom(p
									.getValue())) {
								configurationInitializer.relatedRepresentingClasses
										.add(i.getRepresentingClass());
								configurationInitializer.requiredInitializers
										.add(i);
							}
						}
					} catch (IllegalArgumentException | IllegalAccessException e1) {
						throw new Error(
								"error while accessing configuration parameter "
										+ f.getName() + " in singleton "
										+ bean.getBeanClass().getName());
					}
				}
			}
		}
	}

}
