package laf.core.requestProcessing;

import java.util.Map;

import laf.base.configuration.ConfigurationParameter;
import laf.core.controllerInfo.ControllerType;

/**
 * Configures the {@link RequestProcessor}s used by the
 * {@link SwitchControllerTypeRequestProcessor}
 */
public interface ControllerTypeRequestProcessorsCP
		extends
		ConfigurationParameter<Map<Class<? extends ControllerType>, RequestProcessor>> {

}
