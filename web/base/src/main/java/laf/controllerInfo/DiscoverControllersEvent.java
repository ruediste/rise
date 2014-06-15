package laf.controllerInfo;

/**
 * This event is sent to trigger the controller discovery. Observers can use the
 * methods of this event to register controllers.
 */
public interface DiscoverControllersEvent {

	/**
	 * Add a {@link ControllerInfo}
	 */
	void addController(ControllerInfo info);

	interface ControllerInfoCustomizer {
		void customize(ControllerInfoImpl controllerInfo);

		void customize(ActionMethodInfoImpl actionMethod);

		void customize(ParameterInfoImpl parameterInfo);
	}

	/**
	 * Create and add a {@link ControllerInfo} from the given controller class
	 * and type.
	 */
	ControllerInfo addController(Object type, Class<?> controllerClass,
			ControllerInfoCustomizer customizer);
}
