package laf.configuration;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.FrameworkRootInitializer;
import laf.initialization.CreateInitializersEvent;
import laf.initialization.Initializer;
import laf.initialization.InitializerDependsRelation;

import org.slf4j.Logger;

@Singleton
public class ConfigurationInitializerCreator {

	@Inject
	BeanManager beanManager;

	@Inject
	Instance<Object> instance;

	@Inject
	Logger log;

	/**
	 * An initializer which depends on all initializers created through
	 * {@link ConfigurationParameter}s
	 */
	class ConfigurationInitializer implements Initializer {

		Set<Class<?>> relatedRepresentingClasses = new HashSet<>();

		Set<Initializer> requiredInitializers = new HashSet<>();

		public ConfigurationInitializer() {
			relatedRepresentingClasses.add(FrameworkRootInitializer.class);
		}

		public void addInitializer(Initializer i) {
			requiredInitializers.add(i);
			relatedRepresentingClasses.add(i.getRepresentingClass());
		}

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

		@Override
		public String toString() {
			return "ConfigurationInitializer";
		}

	}

	public void initialize(@Observes CreateInitializersEvent e) {
		log.debug("Scanning configuration parameters in singletons for initializers");
		ConfigurationInitializer configurationInitializer = new ConfigurationInitializer();
		e.addInitializer(configurationInitializer);

		for (Bean<?> bean : beanManager.getBeans(Object.class)) {
			if (!bean.getBeanClass().isAnnotationPresent(Singleton.class)) {
				continue;
			}

			Object beanInstance = null;
			for (Field f : bean.getBeanClass().getFields()) {
				if (ConfigurationParameter.class.isAssignableFrom(f.getType())) {
					log.trace("Handling parameter "
							+ bean.getBeanClass().getName() + "." + f.getName()
							+ ": " + f.getGenericType());

					// obtain bean instance if necessary
					if (beanInstance == null) {
						beanInstance = instance.select(bean.getBeanClass())
								.get();
					}

					try {
						ConfigurationParameter<?> p = (ConfigurationParameter<?>) f
								.get(beanInstance);
						// set the identifier
						p.identifier = f.getDeclaringClass().getName() + "."
								+ f.getName();

						// add eventual initializers
						{
							for (Initializer i : e.createInitializersFrom(p
									.getValue())) {
								log.debug("Found initializer " + i);
								configurationInitializer.addInitializer(i);
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
