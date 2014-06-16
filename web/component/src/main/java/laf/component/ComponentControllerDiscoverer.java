package laf.component;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import laf.controllerInfo.*;

import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class ComponentControllerDiscoverer implements ControllerDiscoverer {

	@Inject
	Logger log;

	@Inject
	BeanManager beanManager;

	@Inject
	ControllerInfoService controllerInfoService;

	@Override
	public void discoverControllers(ControllerInfocCollector collector) {
		ComponentController controllerAnnotation = new ComponentController() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return ComponentController.class;
			}
		};

		for (final Bean<?> bean : beanManager.getBeans(Object.class,
				controllerAnnotation)) {
			log.debug("Found controller " + bean.getBeanClass());
			collector
			.addControllerInfo(new Function<Predicate<Class<?>>, ControllerInfo>() {

				@Override
				public ControllerInfo apply(Predicate<Class<?>> input) {
					return controllerInfoService.createControllerInfo(
							bean.getBeanClass(),
									ComponentController.class, input, null);
				}
			});
		}

	}
}
