package laf.core.controllerInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;

import laf.base.ActionResult;

import com.google.common.base.Predicate;

/**
 * Service to create {@link ControllerInfo}s based on reflection.
 */
@ApplicationScoped
public class ControllerInfoCreationService {

	/**
	 * Customizer to customize {@link ControllerInfo}s after their construction
	 */
	public interface ControllerInfoCustomizer {
		void customize(ControllerInfoImpl controllerInfo);

		void customize(ActionMethodInfoImpl actionMethod);

		void customize(ParameterInfoImpl parameterInfo);
	}

	/**
	 * Base class for {@link ControllerInfoCustomizer}s
	 */
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

	/**
	 * Create a {@link ControllerInfo} using reflection based on the supplied
	 * controller class. The {@link NoActionMethod} annotation is respected.
	 * 
	 * @param controllerClass
	 *            class to create {@link ControllerInfo} for
	 * @param type
	 *            type of the controller info
	 * @param isEmbeddedController
	 *            flag indicating if the info represents an embedded controller
	 * @param customizer
	 *            customizer to customize the created info. Can be null.
	 * @return
	 */
	public ControllerInfoImpl createControllerInfo(Class<?> controllerClass,
			Class<? extends ControllerType> type,
			Predicate<Class<?>> isEmbeddedController,
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
