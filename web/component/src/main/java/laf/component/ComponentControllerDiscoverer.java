package laf.component;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import laf.base.ActionResult;
import laf.controllerInfo.ActionMethodInfoImpl;
import laf.controllerInfo.ControllerDiscoverer;
import laf.controllerInfo.ControllerInfo;
import laf.controllerInfo.ControllerInfoImpl;
import laf.controllerInfo.ControllerInfoService;
import laf.controllerInfo.ControllerInfoService.ControllerInfoCustomizerBase;
import laf.controllerInfo.ParameterInfoImpl;

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
							ComponentController.class, input,
							new Customizer());
				}
			});
		}

	}

	private final static class Customizer extends ControllerInfoCustomizerBase {
		@Override
		public void customize(ControllerInfoImpl controllerInfo) {
			// add reload method
			ActionMethodInfoImpl method = new ActionMethodInfoImpl();
			method.setReturnType(ActionResult.class);
			method.setName(ComponentConstants.reloadMethodName);
			method.setControllerInfo(controllerInfo);
			// the page id
			ParameterInfoImpl param = new ParameterInfoImpl(Long.class);
			param.setMethod(method);
			method.getParameters().add(param);
			controllerInfo.putActionMethodInfo(method);

			// add component action method
			method = new ActionMethodInfoImpl();
			method.setReturnType(ActionResult.class);
			method.setName(ComponentConstants.componentActionMethodName);
			method.setControllerInfo(controllerInfo);
			// the page id
			param = new ParameterInfoImpl(Long.class);
			param.setMethod(method);
			method.getParameters().add(param);
			controllerInfo.putActionMethodInfo(method);
		}
	}
}
