package laf.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.initialization.CreateInitializersEvent;
import laf.initialization.InitializationService;

@Singleton
public class ConfigurationInitializer {

	@Inject
	BeanManager beanManager;

	@Inject
	@Singleton
	Instance<Object> instance;

	@Singleton
	InitializationService initializationService;

	public void initialize(@Observes CreateInitializersEvent e) {
		Singleton singleton = new Singleton() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return Singleton.class;
			}
		};

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
						e.addInitializers(initializationService
								.createInitializers(p.getValue()));
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
