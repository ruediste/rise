package laf.initialization;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

@Singleton
public class SingletonInitializerCreator {

	@Inject
	Logger log;

	@Inject
	BeanManager beanManager;

	@Inject
	Instance<Object> instance;

	@Inject
	InitializationService initializationService;

	public void initialize(@Observes CreateInitializersEvent e) {
		log.debug("Scanning singletons for initializers");
		for (Bean<?> bean : beanManager.getBeans(Object.class)) {
			if (!bean.getBeanClass().isAnnotationPresent(Singleton.class)) {
				continue;
			}

			log.trace("Scanning singleton " + bean.getBeanClass());

			if (initializationService.mightCreateInitializers(bean
					.getBeanClass())) {
				for (Initializer i : e.createInitializersFrom(instance.select(
						bean.getBeanClass()).get())) {
					log.debug("Found initializer " + i);
				}
			}
		}
	}
}
