package laf.initialization;

import java.lang.annotation.Annotation;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SingletonInitializerCreator {

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
			if (initializationService.mightCreateInitializers(bean
					.getBeanClass())) {
				e.createInitializersFrom(instance.select(bean.getBeanClass())
						.get());
			}
		}
	}
}
