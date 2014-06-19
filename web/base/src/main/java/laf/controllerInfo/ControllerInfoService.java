package laf.controllerInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;

import laf.base.ActionResult;
import laf.base.Updating;

import com.google.common.base.Predicate;

@ApplicationScoped
public class ControllerInfoService {

	public interface ControllerInfoCustomizer {
		void customize(ControllerInfoImpl controllerInfo);

		void customize(ActionMethodInfoImpl actionMethod);

		void customize(ParameterInfoImpl parameterInfo);
	}

	public static class ControllerInfoCustomizerBase implements
	ControllerInfoCustomizer {

		@Override
		public void customize(ControllerInfoImpl controllerInfo) {
		}

		@Override
		public void customize(ActionMethodInfoImpl actionMethod) {
		}

		@Override
		public void customize(ParameterInfoImpl parameterInfo) {
		}

	}

	public ControllerInfoImpl createControllerInfo(Class<?> controllerClass,
			Object type, Predicate<Class<?>> isEmbeddedController,
			ControllerInfoCustomizer customizer) {
		ControllerInfoImpl info = new ControllerInfoImpl(controllerClass, type,
				isEmbeddedController.apply(controllerClass));
		for (Method method : info.getControllerClass().getMethods()) {
			if (method.getReturnType() == null) {
				continue;
			}

			if (!(ActionResult.class.isAssignableFrom(method.getReturnType()) || isEmbeddedController
					.apply(method.getReturnType()))) {
				continue;
			}

			if (method.getDeclaringClass().isAnnotationPresent(
					NoActionMethod.class)
					|| method.isAnnotationPresent(NoActionMethod.class)) {
				continue;
			}

			// create method info
			ActionMethodInfoImpl methodInfo = new ActionMethodInfoImpl(method);

			methodInfo.setControllerInfo(info);
			methodInfo.setUpdating(method.isAnnotationPresent(Updating.class));

			// calculate name
			methodInfo
			.setName(info.calculateUnusedMethodName(method.getName()));

			// add to repository
			info.putActionMethodInfo(methodInfo);

			// create parameter infos
			createParameterInfos(methodInfo, customizer);

			if (customizer != null) {
				customizer.customize(methodInfo);
			}
		}
		if (customizer != null) {
			customizer.customize(info);
		}
		return info;
	}

	private void createParameterInfos(ActionMethodInfoImpl actionMethodInfo,
			ControllerInfoCustomizer customizer) {
		for (Type type : actionMethodInfo.getMethod()
				.getGenericParameterTypes()) {
			ParameterInfoImpl parameterInfo = new ParameterInfoImpl(type);
			parameterInfo.setMethod(actionMethodInfo);
			actionMethodInfo.getParameters().add(parameterInfo);
			if (customizer != null) {
				customizer.customize(parameterInfo);
			}
		}
	}
}
