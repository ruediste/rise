package laf.mvc;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import laf.core.controllerInfo.*;
import laf.core.controllerInfo.ControllerInfoCreationService.ControllerInfoCustomizerBase;

import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class MvcControllerDiscoverer implements ControllerDiscoverer {

	@Inject
	Logger log;

	@Inject
	BeanManager beanManager;

	@Inject
	ControllerInfoCreationService controllerInfoService;

	@Inject
	MvcService mvcService;

	private class Customizer extends ControllerInfoCustomizerBase {
		@Override
		public void customize(ActionMethodInfoImpl actionMethod) {
			if (actionMethod.getMethod().isAnnotationPresent(Updating.class)) {
				mvcService.setUpdating(actionMethod, true);
			}
		}
	}

	@Override
	public void discoverControllers(ControllerInfoCollector collector) {
		Controller controllerAnnotation = new Controller() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Controller.class;
			}
		};

		EmbeddedController embeddedControllerAnnotation = new EmbeddedController() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return EmbeddedController.class;
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
									MvcControllerType.class, input,
									new Customizer());
						}
					});
		}

		for (final Bean<?> bean : beanManager.getBeans(Object.class,
				embeddedControllerAnnotation)) {
			log.debug("Found embedded controller " + bean.getBeanClass());
			collector.addEmbeddedControllerInfo(bean.getBeanClass(),
					new Function<Predicate<Class<?>>, ControllerInfo>() {

						@Override
						public ControllerInfo apply(Predicate<Class<?>> input) {
							return controllerInfoService.createControllerInfo(
									bean.getBeanClass(),
									MvcControllerType.class, input,
									new Customizer());
						}
					});
		}

	}
}
